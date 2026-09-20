## Дженерики (Generics) 
— это механизм параметризации типов, позволяющий писать код, который работает с разными типами данных, сохраняя при этом типобезопасность на этапе компиляции.
```java
List list = new ArrayList();
list.add("Hello");
list.add(123);
list.add(new User());

// Компилируется, но падает в runtime!
String s = (String) list.get(1);  // ClassCastException 💥
```

## Стирание типов (Type Erasure)
Дженерики в Java — это фича времени компиляции. После компиляции вся информация о типовых параметрах удаляется (стирается).

Исходный код:
```java
public class Box<T> {
    private T value;
    
    public T get() {
        return value;
    }
    
    public void set(T value) {
        this.value = value;
    }
}

Box<String> box = new Box<>();
box.set("Hello");
String s = box.get();
```
После компиляции (байт-код, эквивалент Java-кода):
```java
public class Box {
    private Object value;  // T заменился на Object
    
    public Object get() {
        return value;
    }
    
    public void set(Object value) {
        this.value = value;
    }
}

Box box = new Box();
box.set("Hello");
String s = (String) box.get();  // компилятор добавил приведение!
```
Причина: обратная совместимость. Дженерики появились в Java 5, а весь код до этого работал с "сырыми" типами. Чтобы старый код продолжал работать, дженерики реализованы как синтаксический сахар над обычными классами.

## Ковариантность и Инвариантность
Это понятия из теории типов, которые описывают, как типы могут заменять друг друга.

**Ковариантность массивов**
Массивы в Java ковариантны — можно подставить массив более конкретного типа.
```java
// Иерархия: String → Object
Object[] objects = new String[10];  // ✅ OK! Массив String можно присвоить Object[]

objects[0] = "Hello";  // Работает
objects[1] = "World";  // Работает

// Но это опасно!
objects[2] = new Integer(42);  // Компилируется, но...
// ArrayStoreException во время выполнения!
```
## **Инвариантность дженериков**
```java
// Иерархия: String → Object
List<Object> objects = new ArrayList<String>();  // ❌ ОШИБКА!

// Нельзя присвоить List<String> к List<Object>

// Если бы дженерики были ковариантны:
List<Object> objects = new ArrayList<String>();
objects.add(42);  // Компилятор разрешит (Integer extends Object)
// Но список на самом деле List<String>!
// ClassCastException при попытке получить String
```

**Главное правило**:
* Массивы — ковариантны (но небезопасно)
* Дженерики — инвариантны (безопасно)
* Wildcards (? extends, ? super) — позволяют управлять ковариантностью/контрвариантностью в дженериках

**Можно ли использовать примитивные типы как параметры дженериков?**  
Нет, примитивные типы нельзя использовать как параметры дженериков. Это фундаментальное ограничение Java. Во время выполнения дженерики стираются до Object. Дженерики работают только с ссылочными типами.

## Raw types и почему их следует избегать
1. Потеря безопасности типов на этапе компиляции
2. Несовместимость с параметризованными типами
```java
List list = new ArrayList();  // Raw type
list.add("Hello");
list.add(42);  // Компилятор пропустит!

String s = (String) list.get(1);  // ClassCastException во время выполнения!
```
**Что произойдёт при попытке new T()**  
Ошибка компиляции. Нельзя создать экземпляр параметра типа напрямую.   
Из-за type erasure — во время выполнения T стирается до Object, и JVM не знает, какой класс инстанцировать.

## Recursive type bound (Рекурсивное ограничение типа)
— это правило: "Тип должен уметь делать что-то с себе подобными".
```java
public static <T> T max(T a, T b) {
    return a.compareTo(b) > 0 ? a : b;  // ❌ ОШИБКА!
}
Проблема: Компилятор не знает, что у типа T есть метод compareTo(). 
Это может быть любой объект — String, Integer, Date, или ваш класс User.

public static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) > 0 ? a : b;  // ✅ Теперь OK!
}
```
Читается так: "T — это тип, который реализует Comparable<T>", то есть умеет сравнивать себя с другим объектом того же типа.

##  Bridge methods 
Синтетические методы, которые компилятор создаёт для сохранения полиморфизма после type erasure.
```java
public class Printer<T> {
    public void print(T data) {
        System.out.println(data);
    }
}

public class StringPrinter extends Printer<String> {
    @Override
    public void print(String data) {  // Хотим печатать только String
        System.out.println("String: " + data);
    }
}
// JVM видит:
// В Printer есть метод print(Object)
// В StringPrinter есть метод print(String)
// Это разные методы (разные сигнатуры)!
// Переопределения не произошло! Полиморфизм сломан!
// Стирание типов
public class StringPrinter extends Printer {
    
    // Ваш метод
    public void print(String data) {
        System.out.println("String: " + data);
    }
    
    // Bridge method (создан компилятором автоматически!)
    public void print(Object data) {
        print((String) data);  // Переводит Object → String
    }
}
```

## Несколько ограничений (bounds) для одного параметра типа
Да, можно. Используется синтаксис T extends A & B & C.
```java
// T должен быть подтипом и Serializable, и Comparable<T>
public <T extends Serializable & Comparable<T>> void process(T item) {
    item.compareTo(item);  // ✅ Метод Comparable доступен
    // item можно сериализовать
}
```
Правила:  
* Максимум один класс в bounds (и он должен быть первым)  
* Интерфейсов может быть сколько угодно


