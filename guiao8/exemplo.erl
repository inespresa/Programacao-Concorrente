-module(exemplo).
-export([recebe/0, envia/2]).

f() -> 
    receive
        {Request, From} -> io:format('msg ~p~n', [Request])
    end,
    f().

recebe() -> spawn(fun() -> f() end).

envia(To, Msg) -> To ! {Msg, self()}.