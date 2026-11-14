## ForkJoinPool 
для задач divide & conquer: задача рекурсивно делится на подзадачи, результаты объединяются.  
Главное отличие от ThreadPoolExecutor — work-stealing вместо одной общей очереди.
Где используется под капотом: parallelStream(), Arrays.parallelSort(), CompletableFuture.*Async(), планировщик виртуальных потоков (JDK 21+).

**Как работает:**
```java
Worker-1: [t1][t2][t3] ← владелец берёт отсюда (top, LIFO)
Worker-2: []            ← вор берёт отсюда (base, FIFO) → t1
Worker-3: [t7]
```
* У каждого потока своя двусторонняя очередь (deque), а не одна общая → нет конкуренции за один лок.
* Владелец кладёт и берёт с одного конца (LIFO) → свежая задача, горячий кэш.
* Вор крадёт с противоположного конца (FIFO) → самый старый и обычно самый крупный кусок работы → редкие кражи,  
хороший балансировки при неравномерной рекурсии.

**Классы задач:**  
* RecursiveTask<V> - нужен результат (max, sum, sort)
* RecursiveAction - сайд-эффект (обход дерева, обновление массива)

**Методы задачи (ForkJoinTask):**  
* fork() - асинхронно положить в очередь текущего worker'а
* compute() - выполнение в текущем потоке
* join() - дождаться результата

**Методы пула:**  
* invoke(task) - Синхронный: вызывающий поток блокируется до завершения(возвращает результат)
* submit(task) - Асинхронный: ставит задачу в пул, не ждёт(ForkJoinTask<V> (это Future<V> — результат позже через get()))
* execute(task) - Асинхронный «запустить и забыть»(void (результат и исключения недоступны))

Пример RecursiveTask<Long>
```java
public class ArraySumTask extends RecursiveTask<Long> {

    private static final int THRESHOLD = 500;

    private final long[] array;
    private final int start;
    private final int end;

    public ArraySumTask(long[] array, int start, int end) {
    this.array = array;
    this.start = start;
    this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        if (length <= THRESHOLD) {
           return sum();
        }
        
        int middle = start + length / 2;
        ArraySumTask left = new ArraySumTask(array, start, middle);
        ArraySumTask right = new ArraySumTask(array, middle, end);
        
        left.fork();
        long rightResult = right.compute();
        long leftResult = left.join();
        
        return leftResult + rightResult;
    }
    
    private long sum() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += array[i];
        }
        return sum;
    }
}
```

