-module(ex1).
-export([create_account/2, 
        close_account/2, 
        login/2, 
        logout/1, 
        online/0,
        start/0]).

create_account(Username, Passwd) -> 
    ?MODULE ! {create_account, Username, Passwd, self()},
    receive {Msg, _From} -> Msg end.

close_account(Username, Passwd) ->
    ?MODULE ! {close_account, Username, Passwd, self()},
    receive {Msg, _From} -> Msg end.

login(Username, Passwd) ->
    ?MODULE ! {login, Username, Passwd, self()},
    receive {Msg, _From} -> Msg end.

logout(Username) ->
    ?MODULE ! {logout, Username, self()},
    receive {Msg, _From} -> Msg end.

online() -> 
    ?MODULE ! {online, self()},
    receive {Msg, _From} -> Msg end.

start() -> register(?MODULE,spawn(fun() -> server(#{}) end)).

server(Users) ->
    receive
        {create_account, Username, Passwd, From} ->
            Cond = maps:is_key(Username, Users),
            if 
                Cond ->
                    From ! {user_exist, self()},
                    NewUsers = Users;
                true -> 
                    NewUsers = maps:put(Username, {Passwd, false}, Users),
                    From ! {ok, self()}
            end,
            server(NewUsers);
        {close_account, Username, Passwd, From} ->
            case maps:get(Username, Users) of
                {Passwd, _} ->
                    NewUsers = maps:remove(Username, Users),
                    From ! {ok, self()};
                _ -> 
                    NewUsers = Users,
                    From ! {invalid, self()}
            end,
            server(NewUsers);
        {login, Username, Passwd, From} ->
            case maps:get(Username, Users, invalid) of
                {Passwd, _} -> 
                    NewUsers = Users#{Username := {Passwd, true}},
                    From ! {ok, self()};
                _ ->
                    NewUsers = Users,
                    From ! {invalid, self()}
            end,
            server(NewUsers);
        {logout, Username, From} ->
            case maps:get(Username, Users, invalid) of
                {Passwd, true} -> 
                    NewUsers = Users#{Username := {Passwd, false}},
                    From ! {ok, self()};
                invalid ->
                    NewUsers = Users,
                    From ! {invalid, self()}
            end,
            server(NewUsers);
        {online, From} ->
            From ! {[User || {User, {_, true}} <- maps:to_list(Users)], self()},
            server(Users)
    end.





