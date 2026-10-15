![img.png](/src/main/resources/images/exception.png)
* **Checked exception** — исключение, которое проверяется компилятором. Оно сигнализирует о предсказуемых внешних проблемах, от которых программа не может защититься сама.  
* **Unchecked exception** — исключение, которое не проверяется компилятором. Наследуется от RuntimeException. Сигнализирует о багах в коде.  
* **Throwable** — это базовый класс для всех ошибок и исключений в Java. Только объекты этого класса (и его наследников) могут быть выброшены через throw или перехвачены через catch.  

**Когда finally НЕ выполняется**:
1. System.exit()
2. Ошибка JVM (Error)
3. Поток прерван/убит
4. Бесконечный цикл в try

**Подавленные исключения (Suppressed Exceptions)**  
это исключение, которое возникло, но было "подавлено" другим исключением, которое стало основным. Это механизм сохранения информации о всех возникших исключениях, а не только о последнем.

**Когда стоит создавать свои исключения?**
1. Нужно передать бизнес-смысл ошибки
2. Нужно различать типы ошибок на уровне типов

Достаточно наследоваться от Exception (checked) или RuntimeException (unchecked).  
**Используйте Exception (checked), когда**:
* Проблема внешняя и её можно обработать  
**Используйте RuntimeException (unchecked), когда**:  
* Проблема внутренняя — баг в коде   
**Checked исключения засоряют API**, заставляют писать throws на всех уровнях, и часто их просто "пробрасывают дальше" без реальной обработки.  
**По умолчанию используйте RuntimeException**.
Используйте checked Exception только если уверены, что вызывающий код может и должен обработать эту ошибку.

## Оборачивание (wrapping) исключений  
это создание нового исключения, которое содержит внутри себя оригинальное как причину (cause).  
```java
// Низкоуровневый код
public void readFromDatabase() throws SQLException {
    // ...
    throw new SQLException("Connection refused");
}

// Высокоуровневый код
public User findUser(Long id) throws UserNotFoundException {
    try {
        return readFromDatabase();
    } catch (SQLException e) {
        // Оборачиваем в наше исключение, сохраняя причину
        throw new UserNotFoundException("Cannot find user: " + id, e);
    }
}
```

**Что произойдёт, если в блоке finally тоже возникнет исключение**?  
Бросится исключение из finally, а из try потеряется!  
Решение:  
Try-with-resources сохраняет оба исключения — главное из try, а из close() становится подавленным (suppressed). 
```java
Сценарий 1:
try: IOException ──────┐
                       │
catch: SQLException ───┤→ Бросается SQLException, IOException потеряна ❌

Сценарий 2:
try: IOException ──────┐
                       │
catch: SQLException ───┤
                       │
finally: RuntimeEx ────┤→ Бросается RuntimeException, оба потеряны ❌❌

Сценарий 3 (правильно):
try: IOException ──────┐
                       ├→ Бросается SQLException + IOException как cause ✅
catch: SQLException ───┘   (сохранена через конструктор)
```
## exception chaining
механизм связывания исключений через cause (причину). Позволяет сохранить низкоуровневое исключение внутри высокоуровневого.  
Сохранить информацию о первоначальной ошибке/Предоставить высокоуровневый API
```java
// Низкоуровневый код
try {
    database.query("SELECT ...");
} catch (SQLException e) {
    // Оборачиваем в наше исключение, сохраняя причину
    throw new DataAccessException("Database error", e);  // ← e как cause
}
// Пользователь нашего API видит только DataAccessException
// Но при необходимости может добраться до оригинальной SQLException
```




