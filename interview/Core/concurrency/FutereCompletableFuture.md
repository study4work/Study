## **Future**:
Future — это обещание (promise) получить результат асинхронной операции в будущем. Когда вы запускаете задачу через ExecutorService, вы получаете Future — объект, который:
* Пока еще не содержит результата (задача выполняется в другом потоке)
* Позволяет проверить статус (выполнена ли задача)
* Позволяет получить результат (блокируясь, если задача еще не готова)
* Позволяет отменить задачу (с переменным успехом)

#### Базовый API
```java
public interface Future<V> {
    boolean cancel(boolean mayInterruptIfRunning);  // Отменить задачу
    boolean isCancelled();                          // Отменена ли?
    boolean isDone();                               // Завершена ли (успешно, с ошибкой или отменена)?
    V get() throws InterruptedException, ExecutionException;  // Получить результат (блокирующий)
    V get(long timeout, TimeUnit unit)              // Получить результат с таймаутом
        throws InterruptedException, ExecutionException, TimeoutException;
}
```
Future — это только интерфейс. Реальные реализации создаются через ExecutorService.submit().
#### Базовый пример с ExecutorService
```java
ExecutorService executor = Executors.newFixedThreadPool(2);

// Запускаем асинхронную задачу
Future<String> future = executor.submit(() -> {
    Thread.sleep(2000); // Имитация долгой работы
    return "Результат задачи";
});

// Пока задача выполняется, мы можем делать другую работу
System.out.println("Задача запущена, делаем другую работу...");

// Получаем результат (блокируемся, если задача еще не готова)
try {
    String result = future.get(); // Будет ждать до 2 секунд
    System.out.println("Получили: " + result);
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
} finally {
    executor.shutdown();
}
```
#### Нюансы и подводные камни
1. Блокирующий get()
2. Исключения "проглатываются".  
   Если в задаче произошло исключение, оно не упадет в консоль сразу. Оно "заморозится" внутри Future и выбросится только при вызове get() в виде ExecutionException.  
   Нюанс: Если вы никогда не вызовете get(), вы никогда не узнаете об ошибке. Задача "тихо" упадет, и вы об этом не узнаете.
3. Нет коллбэков и цепочек
   Вы не можете сказать "когда задача завершится, выполни вот это". Вам приходится:  
   Либо блокироваться в get() и продолжать выполнение  
   Либо крутить цикл while(!future.isDone()) { ... } (polling), что неэффективно  


## **CompletableFuture**:
Позволяет навешивать коллбэки (функции обратного вызова), которые сработают автоматически, когда результат будет готов. Поток не блокируется.  
Позволяет обрабатывать ошибки декларативно, не прерывая цепочку.

#### Базовый API и примеры
Создание (Запуск асинхронных задач)
* supplyAsync(Supplier) — выполняет код и возвращает результат.
* runAsync(Runnable) — выполняет код, результат void.
```java
// Асинхронно считаем сумму
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    sleep(1000); // имитация долгой работы
    return 2 + 2;
});
```
#### Трансформация и цепочки (Callbacks)
* thenApply(Function) — map. Принимает результат, возвращает новый результат.
* thenAccept(Consumer) — потребляет результат, ничего не возвращает (конечная точка).
* thenRun(Runnable) — игнорирует результат предыдущего шага, просто запускает действие.
```java
CompletableFuture.supplyAsync(() -> "Hello")
    .thenApply(String::toUpperCase)       // "HELLO"
    .thenApply(s -> s + " WORLD")         // "HELLO WORLD"
    .thenAccept(System.out::println);     // Выведет в консоль
```
#### Комбинирование нескольких Future
* thenCombine(other, BiFunction) — ждет завершения двух независимых Future и объединяет их результаты.
* allOf(CompletableFuture...) — ждет завершения всех переданных Future (возвращает CompletableFuture<Void>).
* anyOf(CompletableFuture...) — завершается, когда завершится первое (любое) из Future.
```java
CompletableFuture<String> fetchUser = CompletableFuture.supplyAsync(() -> "User123");
CompletableFuture<Integer> fetchAge = CompletableFuture.supplyAsync(() -> 25);

// Ждем обоих и объединяем
CompletableFuture<String> combined = fetchUser.thenCombine(fetchAge, 
    (user, age) -> user + " is " + age + " years old");
```
#### Обработка исключений
* exceptionally(Function) — срабатывает только если была ошибка (аналог catch).
* handle(BiFunction) — срабатывает всегда (и при успехе, и при ошибке).
* whenComplete(BiConsumer) — срабатывает всегда, но не может изменить результат (только побочные эффекты, логирование).
```java
CompletableFuture.supplyAsync(() -> {
    if (true) throw new RuntimeException("Oops!");
    return 42;
}).exceptionally(ex -> {
    System.out.println("Error: " + ex.getMessage());
    return -1; // Возвращаем дефолтное значение
});
```
#### Нюансы
* thenApply — выполняется в том же потоке, который завершил предыдущий этап (или в потоке, который вызвал complete()). 
* thenApplyAsync — всегда передается в пул потоков (Executor) для выполнения.
* Если в цепочке CompletableFuture произошло исключение, и вы не добавили обработчик (exceptionally, handle), исключение не упадет в консоль сразу. Оно "заморозится" внутри объекта. Если вы никогда не вызовете .get() или .join(), вы никогда не узнаете об ошибке.
  Решение: Всегда завершайте пайплайн обработчиком ошибок или логгером в whenComplete.
* thenApply возвращает обычное значение. Если ваша лямбда возвращает другой CompletableFuture, вы получите CompletableFuture<CompletableFuture<T>> (вложенность).
* thenCompose "разворачивает" вложенные Future, чтобы получить плоский CompletableFuture<T>.
  Пример: Если getUser() возвращает Future<User>, а getOrders(User) возвращает Future<List<Order>>, то для их связывания нужен именно thenCompose.

**Когда использовать Future:**  
* Простая асинхронная задача — нужно запустить и забрать результат.
* Работа с legacy-кодом — старые API возвращают Future.
* Минимальные зависимости — не нужен весь API CompletableFuture.
* Контроль над пулом потоков — явно указываешь ExecutorService.  

**Когда использовать CompletableFuture:**  
* Цепочки асинхронных операций — результат одного шака передаётся в следующий.
* Параллельное выполнение нескольких задач — allOf(), anyOf().
* Неблокирующие колбэки — не хочешь блокировать поток через get().
* Обработка ошибок в конвейере — централизованная обработка через exceptionally().
* Реактивные паттерны — комбинация с Stream API, асинхронные конвейеры.