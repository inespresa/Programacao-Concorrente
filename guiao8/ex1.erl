-module(ex1).
-export([create_account/2, 
        close_account/2, 
        login/2, 
        logout/1, 
        online/0,
        start/0]).

create_account(Username, Passwd) -> 
    ?MODULE ! {create_account, Username, Passwd, self()},
    receive {Msg, From} -> Msg end.

close_account(Username, Passwd) ->
    ?MODULE ! {close_account, Username, Passwd, self()},
    receive {Msg, From} -> Msg end.

login(Username, Passwd) ->
    ?MODULE ! {}

start() -> register(?MODULE,spawn(fun() -> server(#{}) end)).

server(Users) ->
    receive
        {create_account, Username, Passwd, From} ->
            if 
                maps:is_key(Username, Users) ->
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
                    NewUsers = map:remove(Username, Users),
                    From ! {ok, self()};
                _ -> 
                    NewUsers = Users,
                    From ! {invalid, self()}
            end,
            server(NewUsers);


