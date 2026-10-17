## Exchanger 
— это упрощенная версия барьера для ровно двух потоков, которые обмениваются данными в точке встречи.

```java
Exchanger<List<String>> exchanger = new Exchanger<>();

/* Производитель накапливает данные в буфере и отдаёт его потребителю */
Thread producer = new Thread(() -> {
    List<String> buffer = new ArrayList<>();  // Начинаем с пустого буфера
    try {
        while (true) {
            buffer.add(readNextItem());  // Наполняем буфер данными
            if (buffer.size() == BATCH_SIZE) { // Буфер заполнен?
                /* Отдаём полный буфер, получаем пустой */
                buffer = exchanger.exchange(buffer);
                buffer.clear();  // Очищаем на всякий случай
            }
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

/* Потребитель берёт полный буфер, отдаёт пустой */
Thread consumer = new Thread(() -> {
    List<String> buffer = new ArrayList<>();  // Начинаем с пустого буфера
    try {
        while (true) {
            /* Получаем полный буфер, отдаём пустой */
            buffer = exchanger.exchange(buffer);
            processAll(buffer);  // Обрабатываем полученные данные
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});
```

### Производитель (Producer)
1. Производитель начинает с пустого буфера
2. Читает данные по одному (readNextItem()) и добавляет в буфер
3.  Когда буфер достигает размера BATCH_SIZE (например, 100 элементов):
* Вызывает exchanger.exchange(buffer)
* БЛОКИРУЕТСЯ и ждет, пока потребитель придет за буфером
* Когда потребитель приходит, производитель отдает полный буфер и получает пустой
4. Очищает полученный буфер и продолжает наполнять его снова

### Потребитель (Consumer)
1. Потребитель начинает с пустого буфера
2. Вызывает exchanger.exchange(buffer) — БЛОКИРУЕТСЯ и ждет полного буфера
3. Когда производитель приносит полный буфер:
* Потребитель отдает свой пустой буфер
* Получает полный буфер от производителя
4. Обрабатывает все данные из полного буфера (processAll(buffer))
5. Цикл повторяется — потребитель снова ждет следующий полный буфер

