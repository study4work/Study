## CyclicBarrier

— циклический барьер
N потоков ждут друг друга в одной точке. Когда все пришли — все идут дальше. Можно использовать повторно (в отличие от latch).

```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    System.out.println("Все собрались, начинаем!");
});

public void doPhase() throws Exception {
    // фаза 1 работы
    barrier.await();   // ждём остальных
    
    // фаза 2 работы
    barrier.await();   // снова ждём
    
    // фаза 3 работы
    barrier.await();
}
```

Сценарий: параллельные вычисления с фазами (например, матричные операции).
![img.png](/src/main/resources/images/cyclicbarr.png)