-module(ex1).
-export([create/0, enqueue/2, dequeue/1]).

create() -> [].

enqueue(Queue, Item) -> Queue ++ [Item].

dequeue([]) -> empty;
dequeue([Item | Tail]) -> {Item, Tail}.