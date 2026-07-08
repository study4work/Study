![img.png](/src/main/resources/images/excecutors.png)

## Виды Executor'ов из класса Executors

### newFixedThreadPool
![img.png](/src/main/resources/images/executor2.png)
Когда использовать:  
✅ Когда нагрузка стабильная и предсказуемая  
✅ Когда нужно ограниченное количество потоков  
✅ Для CPU-bound задач (вычисления)  

❌ НЕ для production! (unbounded queue → OOM)

### newCachedThreadPool
![img.png](/src/main/resources/images/execut3.png)
Когда использовать:  
✅ Для коротких async задач
✅ Когда нагрузка непостоянная
✅ Когда задач мало в каждый момент времени

❌ НЕ для production! (может создать Integer.MAX_VALUE потоков)
❌ НЕ для долгих задач

### newSingleThreadExecutor
![img.png](/src/main/resources/images/execut4.png)

Когда использовать:  
✅ Когда нужна гарантия последовательного выполнения  
✅ Для задач, которые не должны выполняться параллельно  
✅ Для ведения логов, аудита  
✅ Когда нужен один worker  

❌ НЕ для production! (unbounded queue → OOM)

### newScheduledThreadPool  
![img.png](/src/main/resources/images/execut5.png)
Когда использовать:  
✅ Для cron-подобных задач  
✅ Для периодических проверок (health check)  
✅ Для heartbeat, keep-alive  
✅ Для отложенных задач  
✅ Для cleanup задач  

### newWorkStealingPool 
![img.png](/src/main/resources/images/execut6.png)

Когда использовать:
✅ Для рекурсивных задач (divide and conquer)
✅ Для параллельных вычислений (parallel streams)
✅ Когда задачи разного размера
✅ Для CPU-bound задач

❌ Не для I/O-bound задач

### newVirtualThreadPerTaskExecutor
![img.png](/src/main/resources/images/execute7.png)

Когда использовать:  
✅ Для I/O-bound задач (основной use case!)  
✅ Для миллионов одновременных подключений  
✅ Когда нужен простой синхронный код  
✅ Для микросервисов с большим количеством запросов  
 
❌ НЕ для CPU-bound задач (используйте FixedThreadPool)  
❌ Если используется synchronized → замените на ReentrantLock  

Примеры использования:
```java
// Executor — минимальный интерфейс
Executor executor = Runnable::run;  // синхронное выполнение
executor.execute(() -> System.out.println("Hello"));

// Executor — через ThreadPool
Executor executor = Executors.newFixedThreadPool(5);
executor.execute(() -> System.out.println("Task"));
// ❌ Нельзя получить Future
// ❌ Нельзя shutdown()
// ❌ Нельзя настроить пул

// ExecutorService — расширенный интерфейс
ExecutorService service = Executors.newFixedThreadPool(5);
Future<?> future = service.submit(() -> "result");
service.shutdown();  // ✅ можно shutdown

// ThreadPoolExecutor — полная реализация
ThreadPoolExecutor tpe = new ThreadPoolExecutor(
    5, 10, 60, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100)
);
// ✅ Все настройки
// ✅ Все метрики
// ✅ Hooks: beforeExecute(), afterExecute()
// ✅ Rejection policy
```