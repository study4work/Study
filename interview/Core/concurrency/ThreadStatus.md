#### NEW -> Создан, но start() не вызван -> new Thread(...)
#### RUNNABLE -> Готов к выполнению или выполняется -> start(), окончание ожидания  
#### BLOCKED -> Ждёт монитор для входа в synchronized -> Попытка войти в занятый synchronized  
#### WAITING -> Ждёт уведомления неограниченно долго -> wait(), join(), LockSupport.park()
#### TIMED_WAITING -> Ждёт уведомления с таймаутом  -> sleep(ms), wait(ms), join(ms), parkNanos
#### TERMINATED -> run() завершился (нормально или с исключением) -> Выход из run()

1. NEW  
Поток как объект существует, но JVM его ещё не зарегистрировала в планировщике.
```java
Thread t = new Thread(() -> System.out.println("hi"));
System.out.println(t.getState()); // NEW
// Поток ещё не запущен — никакого кода не выполняется
```
2. RUNNABLE  
Поток готов выполняться или выполняется, но включает в себя и ожидание I/O с точки зрения ОС. То есть RUNNABLE в Java ≠ «фактически крутится на CPU».
```java
Thread t = new Thread(() -> {
    while (true) { /* CPU-bound работа */ }
});
t.start();
Thread.sleep(50);
System.out.println(t.getState()); // RUNNABLE (и будет таким всегда)
```
3. BLOCKED  
Поток пытается войти в synchronized-блок, но монитор уже занят другим потоком.  
**Ключевые свойства BLOCKED:**
   * Возникает только из-за synchronized (включая synchronized методы).
   * НЕ возникает из-за ReentrantLock, Semaphore, CountDownLatch и других java.util.concurrent примитивов — они используют LockSupport.park(), а это WAITING.
   * Поток в BLOCKED не реагирует на interrupt() — прерывание не выбьет его из ожидания монитора. Это частый источник багов.  
4. WAITING  
Поток ждёт явного сигнала от другого потока. Ждать может бесконечно долго, пока его не разбудят  
**Способы попасть в WAITING:** n 
   * Object.wait() — классический wait/notify
   * Thread.join() без таймаута — ждём завершения другого потока:
   * LockSupport.park() для ожидания. А park() переводит поток в WAITING, а не в BLOCKED. (ReentrantLock.lock())
5. TIMED_WAITING  
То же, что WAITING, но с таймаутом. Поток либо проснётся по уведомлению, либо по истечении времени — что раньше.  
Методы с вызовом таймаута, ReentrantLock.tryLock(timeout, unit), join(ms), wait(ms), Thread.sleep(ms)
6. TERMINATED  
   Поток закончил run() — нормально или через исключение. Обратного пути нет.