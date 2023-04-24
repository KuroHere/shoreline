# Caspian
---
## Named after the **Caspian** sea.
![caspian_sea](https://user-images.githubusercontent.com/68214996/233989780-8754884f-f678-4260-83fd-59fcf294edd2.png)
## PRE-ALPHA NOTES:
- Lightweight, documented.
- Needs Loader

# Config
---
Client configurations that holds a modifiable value that can be updated via the `ClickGui` or
in the chat via `Command`. Configs must be declared in a class that extends `ConfigContainer`.
The config value cannot be `null`.

## Configurable
Property for configurable data that specifies how it will read/write a `.json` file. 

## ConfigFile
Representation of a local configuration file which holds data for a specific `Configurable` data.

# Event
---
Client event that represents event implementations. Events are handled by the client `EventHandler`
which manages a collection of `EventListener`s.

# EventHandler
Client `Event` handler which handles the invokation of the `Listener` when the events are dispatched.
Classes which contain `EventListener` must be subscribed to the handler manually.

#EventListener
Annotation which indicates that a method should run during an `Event` and should be handled by the 
`EventHandler`. The `Invoker` implementation of `Listener` takes advantage of the `LambdaMetaFactory`
for incredibly fast method handling.
Ex:
```
@EventListener
public void onEvent(Event e)
{
    // your code ...
}
```
