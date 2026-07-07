### Singleton (Одиночка)
Суть: Гарантирует существование только одного экземпляра класса.

```java
// Вариант 1: Ленивая инициализация (не потокобезопасна)
public class Singleton {
    private static Singleton instance;
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

// Вариант 2: Потокобезопасный (synchronized)
public class Singleton {
    private static Singleton instance;
    
    private Singleton() {}
    
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```