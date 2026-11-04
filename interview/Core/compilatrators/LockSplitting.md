## Lock Splitting 
— это JIT-оптимизация, которая разделяет одну большую блокировку на несколько меньших,  
если JIT видит, что разные части синхронизированного блока работают с независимыми данными.

Идея: если два потока обращаются к разным частям защищаемых данных, они не должны конкурировать за один монитор.  
Разделение блокировки увеличивает параллелизм.

**Важно**: Lock Splitting — это противоположность Lock Coarsening. Coarsening объединяет блоки, Splitting разделяет.

```java
public class Stats {
    private int countA = 0;
    private int countB = 0;
    
    // ОДИН монитор на оба счётчика
    public synchronized void incrementA() { countA++; }
    public synchronized void incrementB() { countB++; }
    public synchronized int getCountA() { return countA; }
    public synchronized int getCountB() { return countB; }
}
```
Проблема: если Поток 1 вызывает incrementA(), а Поток 2 вызывает incrementB(), они конкурируют за один монитор, хотя работают с разными данными.
```java
// JIT фактически превращает код в эквивалент:
public class Stats {
    private int countA = 0;
    private int countB = 0;
    private final Object lockA = new Object();  // отдельный монитор
    private final Object lockB = new Object();  // отдельный монитор
    
    public void incrementA() {
        synchronized (lockA) { countA++; }  // только для countA
    }
    public void incrementB() {
        synchronized (lockB) { countB++; }  // только для countB
    }
}
```
Эффект: incrementA() и incrementB() могут выполняться параллельно, так как используют разные мониторы.
