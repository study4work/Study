### Abstract Factory (Абстрактная фабрика)

Суть: Предоставляет интерфейс для создания семейств связанных объектов.
```java
// Продукты для GUI
public interface Button {
    void render();
}

public interface Checkbox {
    void render();
}

// Конкретные продукты — Windows
public class WindowsButton implements Button {
    @Override
    public void render() {
        System.out.println("Windows кнопка");
    }
}

public class WindowsCheckbox implements Checkbox {
    @Override
    public void render() {
        System.out.println("Windows чекбокс");
    }
}

// Конкретные продукты — Mac
public class MacButton implements Button {
    @Override
    public void render() {
        System.out.println("Mac кнопка");
    }
}

public class MacCheckbox implements Checkbox {
    @Override
    public void render() {
        System.out.println("Mac чекбокс");
    }
}

// Абстрактная фабрика
public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

// Конкретные фабрики
public class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

public class MacFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new MacButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}

// Клиентский код
public class Application {
    private Button button;
    private Checkbox checkbox;
    
    public Application(GUIFactory factory) {
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }
    
    public void render() {
        button.render();
        checkbox.render();
    }
}

// Использование:
GUIFactory factory = new WindowsFactory();
Application app = new Application(factory);
app.render();
// Windows кнопка
// Windows чекбокс
```