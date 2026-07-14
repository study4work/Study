![img.png](/src/main/resources/images/StreamApi.png)

## Predicate<T> — проверка условия
```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}
```
```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Фильтруем только чётные числа
List<Integer> evenNumbers = numbers.stream()
        .filter(n -> n % 2 == 0)  // Predicate<Integer>
        .collect(Collectors.toList());

System.out.println(evenNumbers);  // [2, 4, 6, 8, 10]
```
Используется в: filter(), removeIf(), takeWhile(), dropWhile()

## Function<T, R> — преобразование
```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
```
```java
List<String> words = List.of("Hello", "World", "Java");

// Преобразуем строки в их длину
List<Integer> lengths = words.stream()
        .map(s -> s.length())  // Function<String, Integer>
        .collect(Collectors.toList());

System.out.println(lengths);  // [5, 5, 4]
```
Используется в: map(), mapToInt(), mapToLong(), mapToDouble()

## Consumer<T> — действие без результата
```java
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}
```
```java
//peek() — отладка и побочные эффекты
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

// peek() выполняется для каждого элемента, но не изменяет поток
List<Integer> result = numbers.stream()
    .peek(n -> System.out.println("До фильтра: " + n))
    .filter(n -> n % 2 == 0)
    .peek(n -> System.out.println("После фильтра: " + n))
    .map(n -> n * 2)
    .peek(n -> System.out.println("После map: " + n))
    .collect(Collectors.toList());

// Вывод:
// До фильтра: 1
// До фильтра: 2
// После фильтра: 2
// После map: 4
// До фильтра: 3
// До фильтра: 4
// После фильтра: 4
// После map: 8
// До фильтра: 5
```
Используется в: forEach(), peek()

## Supplier<T> — производство значений
```java
@FunctionalInterface
public interface Supplier<T> {
    T get();
}
```
```java
Optional<User> user = findUserById(123);

// Если пользователь не найден, создаём дефолтного
User result = user.orElseGet(() -> new User("Default"));  // Supplier<User>

// Генерируем последовательность дат
Supplier<LocalDate> dateGenerator = () -> LocalDate.now().plusDays(counter++);

Stream<LocalDate> dates = Stream.generate(dateGenerator)
        .limit(7);  // следующие 7 дней
```
Используется в: Stream.generate(), Stream.iterate(), Optional.orElseGet()

## BinaryOperator<T> — бинарная операция
```java
@FunctionalInterface
public interface BinaryOperator<T> extends BiFunction<T, T, T> {
    T apply(T t1, T t2);
}
```
```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

// Сумма всех элементов
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);  // BinaryOperator<Integer>

System.out.println(sum);  // 15

// Или короче:
int sum2 = numbers.stream()
    .reduce(0, Integer::sum);

//--------------------
List<Transaction> transactions = getTransactions();

// Находим транзакцию с максимальной суммой
Optional<Transaction> maxTransaction = transactions.stream()
        .reduce((t1, t2) ->
                t1.getAmount() > t2.getAmount() ? t1 : t2
        );
```
Используется в: reduce(), max(), min()
