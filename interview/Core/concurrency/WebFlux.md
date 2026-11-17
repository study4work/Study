В основе — спецификация Reactive Streams (Publisher/Subscriber/Subscription/Processor) и паттерн «Наблюдатель»:

**Publisher** (издатель) — источник данных. Эмитит элементы (onNext), а затем ровно один терминальный сигнал: onComplete (успех) либо onError (ошибка).  
**Subscriber** (подписчик) — потребитель. Подписывается и обрабатывает три типа сигналов: onNext, onError, onComplete.

#### Обратное давление (Backpressure)
Обратное давление решает проблему: подписчик через Subscription.request(n) сообщает, сколько элементов он готов принять. Издатель не имеет права эмитить больше запрошенного.  
Операторы управления спросом, когда источник быстрее потребителя:  
* onBackpressureBuffer() — буферизовать лишнее (риск OOM при безграничном буфере);
* onBackpressureDrop() — отбрасывать то, что не помещается;
* onBackpressureLatest() — хранить только последний элемент;
* limitRate(n) — ограничить размер запрашиваемых порций.
  
#### Mono и Flux
* Mono<T> — поток из 0 или 1 элемента. Аналог CompletableFuture<T>: получить пользователя по ID, результат одной операции. Mono<Void> сигнализирует о завершении без значения.  
* Flux<T> — поток из 0, 1 или N элементов (потенциально бесконечный): список товаров, лента событий, строки результата запроса.

Способы создания:
```java
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;

// Flux
Flux.just("Apple", "Orange", "Banana");        // из фиксированных значений
Flux.fromIterable(List.of("a", "b", "c"));     // из коллекции
Flux.fromStream(() -> Files.lines(path));       // из Java Stream (ленивое чтение при подписке)
Flux.range(1, 5);                               // 1..5
Flux.interval(Duration.ofSeconds(1));           // тики 0,1,2... каждую секунду (бесконечный)
Flux.generate(sink -> sink.next(compute()));    // программная генерация с учётом request(n)

// Mono
Mono.just("value");                             // готовое значение
Mono.empty();                                    // пустой (только onComplete)
Mono.fromCallable(() -> queryDb());             // ленивое вычисление; разрешает checked-исключения
Mono.fromSupplier(() -> compute());             // ленивое вычисление; без checked-исключений
Mono.error(new RuntimeException("boom"));       // сразу ошибка
```

#### Управление потоками выполнения: Schedulers
По умолчанию реактивный конвейер выполняется в потоке, который вызвал subscribe(). Переключить выполнение позволяют publishOn и subscribeOn:
```java
import reactor.core.scheduler.Schedulers;

Flux.fromCallable(() -> readFileBlocking("data.txt")) // блокирующий I/O
    .subscribeOn(Schedulers.boundedElastic())          // источник — на пуле для блокирующих операций
    .map(content -> content.length())
    .publishOn(Schedulers.parallel())                  // дальше — на пуле для CPU-задач
    .subscribe(len -> System.out.println("длина: " + len));
```
* subscribeOn(scheduler) — где выполняется подписка и источник (влияет на весь восходящий поток, не важно, в каком месте цепочки стоит).
* publishOn(scheduler) — переключает поток для операторов ниже по цепочке.  
**!Правило:** блокирующие вызовы оборачивайте и пускайте на boundedElastic() — иначе вы заблокируете малочисленные потоки реактивного движка и «заморозите» всё приложение.

```java
 * Подсказки:
 * - Mono.just(value) — Mono с одним значением
 * - Flux.just(values...) — Flux с несколькими значениями
 * - flux.map(fn) — трансформация каждого элемента
 * - flux.filter(predicate) — фильтрация
 * - flux.flatMap(fn) — разворачивает каждый элемент в поток (порядок НЕ гарантирован)
 * - flux.concatMap(fn) — разворачивает каждый элемент в поток с сохранением порядка
 * - Flux.merge(f1, f2) — объединяет потоки параллельно (не гарантирует порядок)
 * - Flux.concat(f1, f2) — объединяет потоки последовательно (порядок сохраняется)
 * - Flux.zip(f1, f2, combiner) — комбинирует попарно
 * - flux.onErrorReturn(value) — при ошибке — вернуть значение
 * - flux.onErrorResume(fn) — при ошибке — переключиться на другой поток
 * - flux.retry(n) — повторно подписаться при ошибке (n попыток)
 * - Mono.fromSupplier(supplier).subscribeOn(Schedulers.boundedElastic()) — блокирующий вызов на пуле boundedElastic
```