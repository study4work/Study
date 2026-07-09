### Synchronized 
— это встроенный механизм Java для обеспечения потокобезопасности (thread safety). Он гарантирует, что к определенному блоку кода одновременно может получить доступ только один поток. 

Основан на концепции монитора — у каждого объекта в Java есть встроенная блокировка.

### Способы применения synchronized

1. Синхронизированный метод экземпляра  
```java
public class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
}
```
На чём блокируется? На текущем объекте (this).
Если два потока вызывают increment() на одном и том же экземпляре Counter, второй будет ждать. Если на разных — блокировки не будет.
```java
Counter c1 = new Counter();
Counter c2 = new Counter();

c1.increment(); // блокирует c1
c2.increment(); // НЕ блокируется, работает параллельно с c1
```

2. Синхронизированный блок (synchronized block)
```java
public void doSomething() {
    // код, который может выполняться параллельно
    System.out.println("Step 1");
    
    synchronized (this) {
        // критическая секция
        count++;
    }
    
    // снова параллельный код
    System.out.println("Step 3");
}
```
Зачем блок вместо метода?  
Чтобы синхронизировать только критическую часть, а не весь метод (производительность).  
Чтобы заблокировать другой объект, не this.
```java
private final Object lock = new Object();

public void update() {
    synchronized (lock) {
        // работаем с общими данными
    }
}
```

3. Синхронизированный статический метод

```java
public class Singleton {
    private static int instanceCount = 0;
    
    public static synchronized void createInstance() {
        instanceCount++;
    }
}
```
На чём блокируется? На объекте класса (Singleton.class).  
Это другой монитор, чем this. Поэтому статический синхронизированный метод и обычный синхронизированный метод одного класса не блокируют друг друга.

4. Синхронизированный статический блок
```java
public class Config {
    private static Map<String, String> settings;
    
    static {
        synchronized (Config.class) {
            settings = loadFromDisk();
        }
    }
}
```