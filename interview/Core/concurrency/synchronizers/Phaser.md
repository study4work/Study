## Phaser
гибкое расширение CyclicBarrier: число участников меняется во время работы, а фазы нумеруются автоматически.

**Phaser умеет**:
1. Работать с динамическим количеством участников (можно регистрировать и дерегистрировать потоки на лету)
2. Автоматически переходить между фазами (каждый arriveAndAwaitAdvance = переход к следующей фазе)
3. Поддерживать иерархию (можно создавать древовидные структуры для больших систем)

```java
/* Симуляция гонки: разминка → квалификация → 3 круга */
int participants = 5;
Phaser phaser = new Phaser(participants);

for (int i = 0; i < participants; i++) {
    String name = "Участник " + (i + 1);
    new Thread(() -> {
        System.out.println(name + " начал разминку");
        doExercise();
        /* Все ждут окончания разминки */
        //arrive() — "Я прибыл на контрольную точку и не ждет"
        //arriveAndAwaitAdvance() — "Жду, пока все остальные тоже прибудут, затем переходим к следующей фазе"
        phaser.arriveAndAwaitAdvance();

        for (int lap = 1; lap <= 3; lap++) {
            System.out.println(name + " начал круг " + lap);
            doRaceLap();
            /* Все ждут окончания круга */
            //Все 5 участников начинают круг
            //Каждый финиширует в свое время
            //Когда все 5 финишировали → все ждут → переход к итерации 2
            phaser.arriveAndAwaitAdvance();
        }

        System.out.println(name + " финишировал");
        /* покидаем фазер */
        phaser.arriveAndDeregister();
    }).start();
}
```

