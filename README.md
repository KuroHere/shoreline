# Caspian
## Named after the ***Caspian Sea***.
![caspian_sea](https://user-images.githubusercontent.com/68214996/233989780-8754884f-f678-4260-83fd-59fcf294edd2.png)
## PRE-ALPHA NOTES
- ver-1.0-a1
- Lightweight, documented.
- API still UNTESTED and UNFINISHED
- Needs Loader

# Documentation
## `Command`
Client command that runs an executable command based on an `Argument` 
structure with inputs from the chat. Command suggestion and execution is 
managed by the `CommandHandler`. The collection of available client commands 
are listed in the `CommandManager`.

## `Config<T>`
Client configuration that holds a modifiable value that can be updated via 
the `ClickGui` or in the chat via `Command`. Configs are registered using
reflection, so declaration of configs must be in a class that extends 
`ConfigContainer`. The config value cannot be `null`. Ex:
```java
// registered via reflection
final Config<Boolean> booleanConfig = new BooleanConfig("ExampleBoolean", 
            "Example for boolean", false);
```

## `Configurable`
Property for configurable data that specifies how it will read/write a `.json` file. 

## `ConfigFile`
Representation of a local configuration file which holds data for a specific 
`Configurable` data. The `ClientConfiguration` manages the list of client config files to save/load.

## `Event`
Client event that represents event implementations. Events are handled by 
the client `EventHandler` which manages a collection of `EventListener`s. If 
the event implementation is annotated with `Cancelable`, then the event can 
cancel the callback of the event method. Additionally, the event can be 
dispatched during the stages `EventStage.Pre` and `EventStage.Post`.

## `EventHandler`
Client `Event` handler which handles the invokation of the `Listener` when 
the events are dispatched. Classes which contain `EventListener` must be 
subscribed to the handler manually.

## `@EventListener`
Annotation which indicates that a method should run during an `Event` and 
should be handled by the `EventHandler`. The `Invoker` implementation of 
`Listener` takes advantage of the `LambdaMetaFactory` for incredibly fast 
method handling. Ex:
```java
@EventListener
public void onEvent(Event e)
{
    // your code ...
}
```

## `Macro`
Runnable macro with `GLFW` keybinding toggle (i.e. the macro will run every 
keybind press). `Module` keybindings are implemented through the use of 
macros  and handled by the `MacroHandler`. Custom macros can be created with 
`Command` and are managed by the `MacroManager`.

