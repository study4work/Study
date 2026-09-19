## Record 
— это специальный тип класса, который представляет собой неизменяемый контейнер данных.

**Record автоматически создает**:  
✅ Приватные final поля  
✅ Конструктор со всеми полями  
✅ Геттеры (без get префикса)  
✅ equals(), hashCode(), toString()

**Ограничения Records**  
* Нельзя наследоваться
```java
public record User(String name) {}
// public class Admin extends User {}  // ОШИБКА! Records неявно final
```
* Нельзя изменять поля
```java
public record User(String name) {
    public void setName(String newName) {
        // this.name = newName;  // ОШИБКА! Поля final
    }
}
```
* Нельзя объявлять instance-поля
```java
public record User(String name) {
    // private int counter;  // ОШИБКА! Только static поля разрешены
    private static int counter = 0;  // OK
}
```
## Конструкторы в Record
1. Канонический конструктор (полная форма)  
   Это конструктор, параметры которого точно совпадают с компонентами record.
```java
public record User(String name, int age) {
    
    // Переопределяем канонический конструктор
    public User(String name, int age) {
        // Валидация
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        
        // ОБЯЗАТЕЛЬНО: присваиваем все поля вручную
        this.name = name;
        this.age = age;
    }
}
```
2. Компактный конструктор (сокращенная форма)  
  Это синтаксический сахар для канонического. Присваивание полей происходит автоматически.
```java
public record User(String name, int age) {
    
    // Компактный конструктор — без параметров!
    public User {
        // Валидация
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        
        // Присваивание происходит АВТОМАТИЧЕСКИ после этого блока
        // this.name = name;  ← не нужно писать
        // this.age = age;    ← не нужно писать
    }
}
```
3. Дополнительные (перегруженные) конструкторы  
   Можно создать конструкторы с другим набором параметров, но они обязаны делегировать каноническому.
```java
public record User(String name, int age) {
    
    // Дополнительный конструктор
    public User(String name) {
        this(name, 0);  // ОБЯЗАТЕЛЬНО: делегируем каноническому
    }
    
    // Ещё один дополнительный
    public User() {
        this("Unknown", 0);  // ОБЯЗАТЕЛЬНО: делегируем каноническому
    }
}

// Использование
User u1 = new User("Alice", 30);  // Канонический
User u2 = new User("Bob");         // Дополнительный → age = 0
User u3 = new User();              // Дополнительный → "Unknown", 0
```

