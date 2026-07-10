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