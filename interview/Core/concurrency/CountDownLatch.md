## CountDownLatch 
— Одноразовый барьер. Счётчик N, который уменьшается до 0. Потоки ждут, пока счётчик не станет 0.

НЕ реализует Lock  
Задача: ждать, пока N событий произойдут (счётчик N → 0)  
Состояние AQS: обратный счётчик

```java
CountDownLatch latch = new CountDownLatch(3);

// Поток-ожидатель
public void waitForAll() throws InterruptedException {
    latch.await();   // блокируется, пока счётчик != 0
    System.out.println("Все готовы!");
}

// Потоки-работяги (каждый вызывает один раз)
public void doWork() {
    try {
        // работа...
    } finally {
        latch.countDown();   // уменьшить счётчик на 1
    }
}
```

Сценарий: главный поток запускает 3 сервиса и ждёт, пока все они инициализируются
```java
CountDownLatch startupLatch = new CountDownLatch(3);

// В каждом сервисе:
public void start() {
    initialize();
    startupLatch.countDown();
}

// В главном потоке:
startupLatch.await();   // ждём, пока все 3 сервиса стартанут
System.out.println("Приложение готово!");
```