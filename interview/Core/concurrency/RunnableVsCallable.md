![img.png](/src/main/resources/images/runnablecallable.png)

Runnable — базовая задача
```java
public interface Runnable {
    void run();  // не возвращает значение, не бросает checked исключения
}

// Создаём задачу
Runnable task = () -> {
    System.out.println("Выполняю работу в потоке: " + Thread.currentThread().getName());
};

// Способ 1: через Thread
Thread thread = new Thread(task);
thread.start();

// Способ 2: через ExecutorService (рекомендуется)
ExecutorService executor = Executors.newFixedThreadPool(2);
executor.submit(task);
executor.shutdown();
```

Когда использовать:  
Простая задача без возврата значения  
Не нужно пробрасывать checked исключения наружу  
Совместимость со старым кодом (Java 1.0+)  
Ограничения:  
Нельзя вернуть результат  
Нельзя бросить checked exception (придётся ловить внутри run())  

Callable — задача с результатом

```java
public interface Callable<V> {
    V call() throws Exception;  // возвращает V, может бросить Exception
}

// Создаём задачу с результатом
Callable<Integer> task = () -> {
    Thread.sleep(1000);  // имитация долгой работы
    return 42;  // возвращаем значение
};

// Запускаем через ExecutorService
ExecutorService executor = Executors.newFixedThreadPool(2);
Future<Integer> future = executor.submit(task);

// Получаем результат (блокируемся, пока не готов)
Integer result = future.get();  // 42
System.out.println("Результат: " + result);

executor.shutdown();
```

Пример с исключением:
```java
Callable<String> task = () -> {
    if (someCondition) {
        throw new IOException("Ошибка ввода-вывода");  // ✅ можно бросить checked exception
    }
    return "Успех";
};

Future<String> future = executor.submit(task);

try {
    String result = future.get();
} catch (ExecutionException e) {
    // Исключение из call() оборачивается в ExecutionException
    Throwable cause = e.getCause();  // IOException
    System.err.println("Ошибка: " + cause.getMessage());
}
```

Когда использовать:
Нужен результат выполнения (например, вычисление, запрос к БД)
Нужно пробросить checked исключения наружу
Работаете с ExecutorService и Future
Преимущества перед Runnable:
✅ Возвращает значение
✅ Может бросать checked исключения
✅ Лучше интегрируется с Future и асинхронными вычислениями