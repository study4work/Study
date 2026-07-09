### ReentrantLock
— это альтернатива synchronized из пакета java.util.concurrent.locks, появившаяся в Java 5. Он предоставляет те же базовые гарантии (взаимное исключение, видимость), но с гораздо большей гибкостью.

![img.png](/src/main/resources/images/rrentrlock.png)

Synchronized — простой и надёжный, но негибкий:  
Нельзя прервать поток, ждущий блокировку  
Нельзя захватить блокировку с таймаутом  
Нельзя сделать "честную" очередь (FIFO)  
Нельзя освободить блокировку из другого потока  
Нельзя иметь несколько условий ожидания (wait/notify — только одно)  
ReentrantLock решает все эти проблемы.  

Condition — это механизм, который позволяет потоку "уснуть" до выполнения определенного условия и быть разбуженным другим потоком, когда это условие наступит. Это улучшенная замена классическим wait() / notify().  
Condition — это отдельная очередь ожидания, привязанная к Lock. Можно создать сколько угодно таких очередей на одном замке.

```java
class BoundedBuffer {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull  = lock.newCondition();  // условие "не полная"
    private final Condition notEmpty = lock.newCondition();  // условие "не пустая"
    
    private final Object[] items = new Object[5];
    private int count = 0;
    
    // Производитель
    public void put(Object item) throws InterruptedException {
        lock.lock();              // 1. Захватываем замок
        try {
            // 2. Пока буфер полный — спим
            while (count == items.length) {
                notFull.await();  // освобождаем lock и засыпаем
            }
            
            // 3. Кладём элемент
            items[count++] = item;
            
            // 4. Будим потребителей: "эй, есть что брать!"
            notEmpty.signal();
        } finally {
            lock.unlock();        // 5. Освобождаем замок
        }
    }
    
    // Потребитель
    public Object take() throws InterruptedException {
        lock.lock();
        try {
            // Пока буфер пустой — спим
            while (count == 0) {
                notEmpty.await();
            }
            
            Object item = items[--count];
            
            // Будим производителей: "эй, есть место!"
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
```

Что происходит пошагово:  
Сценарий: буфер пуст, потребитель хочет взять.  
Поток-потребитель заходит в take(), захватывает lock.  
Видит count == 0 → заходит в while.  
Вызывает notEmpty.await():  
Освобождает lock (важно! иначе производитель не сможет зайти)  
Засыпает и встаёт в очередь notEmpty  
Приходит производитель, захватывает lock, кладёт элемент.  
Вызывает notEmpty.signal() → будит одного потребителя из очереди notEmpty.  
Потребитель просыпается, снова захватывает lock, проверяет условие в while и идёт дальше.

![img.png](/src/main/resources/images/lock.png)

## ReentrantReadWriteLock 
— лок с разделением на чтение/запись  
Содержит два внутренних лока: ReadLock и WriteLock, оба реализуют Lock  
Задача: много читателей одновременно, но писатель — эксклюзивно  

Правила:  
Read lock могут держать много потоков одновременно  
Write lock — только один поток, и пока он держится, read lock никто не возьмёт  
Если кто-то держит write lock, read lock ждут  
Если есть читатели, писатель ждёт, пока все они освободят read lock  

Подводный камень: писатель может голодать, если постоянно приходят новые читатели. Решение — fair lock: new ReentrantReadWriteLock(true).