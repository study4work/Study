## Command (Команда)

Суть: Инкапсулирует запрос как объект.

```java
// Команда
public interface Command {
    void execute();
    void undo();
}

// Получатель
public class Light {
    private boolean isOn = false;
    private String location;
    
    public Light(String location) {
        this.location = location;
    }
    
    public void turnOn() {
        isOn = true;
        System.out.println(location + " свет включен");
    }
    
    public void turnOff() {
        isOn = false;
        System.out.println(location + " свет выключен");
    }
}

// Конкретные команды
public class LightOnCommand implements Command {
    private Light light;
    
    public LightOnCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        light.turnOn();
    }
    
    @Override
    public void undo() {
        light.turnOff();
    }
}

public class LightOffCommand implements Command {
    private Light light;
    
    public LightOffCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        light.turnOff();
    }
    
    @Override
    public void undo() {
        light.turnOn();
    }
}

// Invoker (вызывающий)
public class RemoteControl {
    private Command command;
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    public void pressButton() {
        command.execute();
    }
    
    public void pressUndoButton() {
        command.undo();
    }
}

// Использование:
Light livingRoomLight = new Light("Гостиная");
Light kitchenLight = new Light("Кухня");

Command livingRoomOn = new LightOnCommand(livingRoomLight);
Command kitchenOff = new LightOffCommand(kitchenLight);

RemoteControl remote = new RemoteControl();

remote.setCommand(livingRoomOn);
remote.pressButton();  // Гостиная свет включен

remote.setCommand(kitchenOff);
remote.pressButton();  // Кухня свет выключен

remote.setCommand(livingRoomOn);
remote.pressUndoButton();  // Гостиная свет выключен
```