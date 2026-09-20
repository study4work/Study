### Producer Extends Consumer Super

Eсли у нас есть некая коллекция, типизированная wildcard с верхней границей (extends) – то это, «продюсер».  
«Он только «продюсирует», предоставляет элемент из контейнера, а сам ничего не принимает».  
Если же у нас коллекция, типизированная wildcard по нижней границе (super) – то это, «потребитель», который «только принимает, а предоставить ничего не может».

## Producer Extends:

• Если параметр производит данные (читаем)  
• Используем <? extends T>  
• Можно только get()

```java
// Метод суммирует числа из списка
public static double sum(List<? extends Number> numbers) {
    double sum = 0;
    for (Number n : numbers) {  // ✅ Можем читать как Number
        sum += n.doubleValue();
    }
    return sum;
}

// Использование
List<Integer> integers = Arrays.asList(1, 2, 3);
List<Double> doubles = Arrays.asList(1.5, 2.5, 3.5);

System.out.println(sum(integers));  // 6.0 ✅
System.out.println(sum(doubles));   // 7.5 ✅

// numbers.add(42);  // ❌ ОШИБКА! Нельзя добавлять (кроме null)
// Потому что мы не знаем точный тип: Integer? Double? Float?
```

## Consumer Super:
• Если параметр потребляет данные (пишем)  
• Используем <? super T>  
• Можно только put()

```java
// Метод добавляет числа в список
public static void addNumbers(List<? super Integer> list) {
    list.add(1);   // ✅ Можем добавлять Integer
    list.add(2);   // ✅ Можем добавлять Integer
    list.add(3);   // ✅ Можем добавлять Integer
    
    // Integer i = list.get(0);  // ❌ ОШИБКА! Нельзя читать как Integer
    // Потому что список может быть List<Number>, List<Object>
    // Мы знаем только, что это супертип Integer
}

// Использование
List<Integer> integers = new ArrayList<>();
List<Number> numbers = new ArrayList<>();
List<Object> objects = new ArrayList<>();

addNumbers(integers);  // ✅ OK
addNumbers(numbers);   // ✅ OK
addNumbers(objects);   // ✅ OK

// После вызова:
System.out.println(integers);  // [1, 2, 3]
System.out.println(numbers);   // [1, 2, 3]
System.out.println(objects);   // [1, 2, 3]
```

**Реальные примеры из JDK**  
Collections.copy()
Collections.addAll()