-module(ex2).
-export([create/0, enqueue/3, dequeue/1]).

create() -> [].

enqueue([], Item, Priority) -> [{Item, Priority}];
enqueue([{Item1, Priority1} | Tail], Item, Priority) ->
    if
        Priority < Priority1 -> [{Item,Priority} | Tail]
        true -> [{Item1, Priority1} | enqueue(Tail, Item, Priority)]
    end.

dequeue([]) -> empty;
dequeue([{Item, Priority} | Tail]) -> {Tail, Item}.
