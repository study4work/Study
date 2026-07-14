## Транзакции 
— это фундаментальный механизм обеспечения целостности данных в реляционных базах данных. JDBC предоставляет полный контроль над транзакциями через интерфейс Connection.

Транзакция — это логическая единица работы с базой данных, которая либо выполняется целиком, либо не выполняется вообще.

Пример: перевод денег между счетами
```java
// Без транзакции — ОПАСНО!
stmt.executeUpdate("UPDATE accounts SET balance = balance - 100 WHERE id = 1");
// ⚠️ Если здесь произойдёт сбой, деньги спишутся, но не зачислятся!
stmt.executeUpdate("UPDATE accounts SET balance = balance + 100 WHERE id = 2");
```

Проблема: если после первого UPDATE произойдёт ошибка (сбой питания, исключение, deadlock), данные окажутся в несогласованном состоянии — деньги спишутся, но не зачислятся.

Решение: обернуть операции в транзакцию:
```java
// С транзакцией — БЕЗОПАСНО!
conn.setAutoCommit(false);  // начинаем транзакцию
try {
    stmt.executeUpdate("UPDATE accounts SET balance = balance - 100 WHERE id = 1");
    stmt.executeUpdate("UPDATE accounts SET balance = balance + 100 WHERE id = 2");
    conn.commit();  // фиксируем изменения
} catch (SQLException e) {
    conn.rollback();  // откатываем все изменения
    throw e;
}
```

## Базовое управление транзакциями в JDBC
1. Auto-commit режим (по умолчанию)
```java
Connection conn = DriverManager.getConnection(url, user, password);

// По умолчанию auto-commit = true
// Каждый SQL-запрос — отдельная транзакция, которая автоматически фиксируется
conn.createStatement().executeUpdate("INSERT INTO users VALUES (1, 'Alice')");
// Транзакция автоматически закоммичена!
```
Проблема: каждый запрос — отдельная транзакция. Нельзя выполнить несколько запросов атомарно.
2. Ручное управление транзакциями
```java
Connection conn = DriverManager.getConnection(url, user, password);

// Отключаем auto-commit
conn.setAutoCommit(false);  // ← начинаем транзакцию

try {
    // Выполняем несколько операций
    PreparedStatement stmt1 = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
    stmt1.setDouble(1, 100);
    stmt1.setInt(2, 1);
    stmt1.executeUpdate();
    
    PreparedStatement stmt2 = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
    stmt2.setDouble(1, 100);
    stmt2.setInt(2, 2);
    stmt2.executeUpdate();
    
    // Фиксируем транзакцию
    conn.commit();
    System.out.println("Перевод выполнен успешно");
    
} catch (SQLException e) {
    // Откатываем транзакцию при ошибке
    conn.rollback();
    System.err.println("Ошибка перевода, изменения отменены: " + e.getMessage());
    throw e;
} finally {
    // Восстанавливаем auto-commit (опционально)
    conn.setAutoCommit(true);
    conn.close();
}
```

## Ключевые методы управления транзакциями
### setAutoCommit(boolean autoCommit)
```java
conn.setAutoCommit(false);  // ручное управление
conn.setAutoCommit(true);   // автоматическое (по умолчанию)
```
Важно: при вызове setAutoCommit(true) происходит автоматический commit текущей транзакции!

## commit()
```java
conn.commit();  // фиксирует все изменения с последнего commit/rollback
```
Сохраняет изменения в БД  
Освобождает блокировки (зависит от уровня изоляции)  
Начинает новую транзакцию (если auto-commit = false)

## rollback()
```java
conn.rollback();  // отменяет все изменения с последнего commit/rollback
```
Отменяет все изменения  
Освобождает блокировки  
Начинает новую транзакцию

## rollback(Savepoint savepoint)
```java
Savepoint sp = conn.setSavepoint("before_update");
try {
    // risky operation
} catch (SQLException e) {
    conn.rollback(sp);  // откатываем до savepoint, а не до начала транзакции
}
```
