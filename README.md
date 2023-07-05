![client_icon](https://user-images.githubusercontent.com/68214996/234266241-451285dc-aaae-4087-bcb8-a52306959b95.png)
---
## Named after the ***Caspian Sea***.
![caspian_sea](https://user-images.githubusercontent.com/68214996/233989780-8754884f-f678-4260-83fd-59fcf294edd2.png)

# Documentation
## `ToggleModule`
Client `Module` that provides the implementation of a toggleable feature.
The `Module` tracks an enabled state which determines whether the `Module`
is currently functioning and whether the `Module` is currently subscribed
to the `EventHandler`.
Ex:
``` java
public class ExampleModule extends ToggleModule
{
    public ExampleModule()
    {
        super("ExampleModule", "Example of module implementation",
                ModuleCategory.TEST);
    }

    // declared with default keybind
    public ExampleModule()
    {
        super("ExampleModule", "Example of module implementation",
                ModuleCategory.TEST, GLFW.GLFW_KEY_TAB);
    }

    @Override
    public void onEnable()
    {
        // onEnable callback
    }

    @Override
    public void onDisable()
    {
        // onDisable callback 
    }

    // module implementation ...
}
```

## `Command`
Client command that runs an executable command based on an `Argument` 
structure with inputs from the chat. Command suggestion and execution is 
managed by the `CommandHandler`. The collection of available client commands 
are listed in the `CommandManager`.

## `Argument`
Commands are essentially made up of an array of `Argument`. This holds a value
which has been converted from the user input in the chat. The value is `null` 
if the input was not given or the input was invalid. Ex:
```java
public class ExampleCommand extends Command
{
    public ExampleCommand()
    {
        super("example", "<arg>", new StringArgument().addSuggestions("foo",
                "bar", "example-suggestion"));
    }

    @Override
    public void runCommand()
    {
        // command implementation ...
    }
}
```

## `Config<T>`
Client configuration that holds a modifiable value that can be updated via 
the `ClickGui` or in the chat via `Command`. Configs are registered using
reflection, so declaration of configs must be in a class that extends 
`ConfigContainer`. The config value cannot be `null`. Ex:
```java
// registered via reflection
Config<Boolean> booleanConfig = new BooleanConfig("ExampleCheckbox", 
        "Example for boolean", false);
Config<Integer> integerConfig = new NumberConfig<>("ExampleIntegerSlider",
        "Example for integer slider", 0, 1, 3);
Config<Float> floatConfig = new NumberConfig<>("ExampleFloatSlider",
        "Example for float slider", 0.0f, 1.0f, 3.0f);
```

## `Configurable`
Property for configurable data that specifies how it will read/write a `.json` 
file. 

## `ConfigFile`
Representation of a local configuration file which holds data for a specific 
`Configurable` data. The `ClientConfiguration` manages the list of client 
config files to save/load.

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

