![img.png](/src/main/resources/images/solid.png)

## S — Single Responsibility Principle (SRP)

![img.png](/src/main/resources/images/singlerespons.png)
 

```java
// ❌ КЛАСС С МНОЖЕСТВЕННОЙ ОТВЕТСТВЕННОСТЬЮ
public class User {
    private String name;
    private String email;
    private String password;
    
    // Конструктор, геттеры, сеттеры...
    
    // Ответственность 1: Валидация
    public boolean validate() {
        if (name == null || name.isEmpty()) return false;
        if (email == null || !email.contains("@")) return false;
        if (password == null || password.length() < 8) return false;
        return true;
    }
    
    // Ответственность 2: Сохранение в БД
    public void saveToDatabase() {
        // JDBC код для сохранения
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        // ... подключение к БД, выполнение запроса ...
    }
    
    // Ответственность 3: Отправка email
    public void sendWelcomeEmail() {
        // SMTP код для отправки email
        // ... создание email сообщения, отправка ...
    }
    
    // Ответственность 4: Логирование
    public void logCreation() {
        System.out.println("User created: " + name);
        // Запись в файл логов
    }
}

// Проблемы:
// • Если изменится формат БД → нужно менять User
// • Если изменится способ отправки email → нужно менять User
// • Если изменится формат валидации → нужно менять User
// • Сложно тестировать (нужно мокировать БД, SMTP, и т.д.)
// • Невозможно переиспользовать валидацию отдельно
```  
```java
// ✅ КЛАСС ТОЛЬКО С ДАННЫМИ (Domain Model)
public class User {
    private String name;
    private String email;
    private String password;
    
    // Конструктор, геттеры, сеттеры...
    // Только данные и базовая логика
}

// ✅ ОТДЕЛЬНЫЙ КЛАСС ДЛЯ ВАЛИДАЦИИ
public class UserValidator {
    public boolean validate(User user) {
        if (user.getName() == null || user.getName().isEmpty()) return false;
        if (user.getEmail() == null || !user.getEmail().contains("@")) return false;
        if (user.getPassword() == null || user.getPassword().length() < 8) return false;
        return true;
    }
}

// ✅ ОТДЕЛЬНЫЙ КЛАСС ДЛЯ СОХРАНЕНИЯ (Repository)
public class UserRepository {
    public void save(User user) {
        // JDBC код для сохранения
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        // ... подключение к БД, выполнение запроса ...
    }
}

// ✅ ОТДЕЛЬНЫЙ КЛАСС ДЛЯ ОТПРАВКИ EMAIL (Service)
public class EmailService {
    public void sendWelcomeEmail(User user) {
        // SMTP код для отправки email
        // ... создание email сообщения, отправка ...
    }
}

// ✅ ОТДЕЛЬНЫЙ КЛАСС ДЛЯ ЛОГИРОВАНИЯ
public class Logger {
    public void logUserCreation(User user) {
        System.out.println("User created: " + user.getName());
        // Запись в файл логов
    }
}

// ✅ ИСПОЛЬЗОВАНИЕ
public class UserService {
    private UserValidator validator = new UserValidator();
    private UserRepository repository = new UserRepository();
    private EmailService emailService = new EmailService();
    private Logger logger = new Logger();
    
    public void createUser(User user) {
        if (!validator.validate(user)) {
            throw new IllegalArgumentException("Invalid user");
        }
        
        repository.save(user);
        emailService.sendWelcomeEmail(user);
        logger.logUserCreation(user);
    }
}

// Преимущества:
// • Каждый класс отвечает за ОДНУ вещь
// • Легко тестировать (можно мокировать зависимости)
// • Легко изменять (изменение БД не затрагивает User)
// • Переиспользуемость (UserValidator можно использовать в других местах)
```

## O — Open/Closed Principle (OCP)
![img.png](/src/main/resources/images/opprincipe.png)

```java
// ❌ КЛАСС, КОТОРЫЙ НУЖНО МЕНЯТЬ ПРИ ДОБАВЛЕНИИ НОВЫХ ТИПОВ
public class DiscountCalculator {
    
    public double calculateDiscount(Customer customer) {
        if (customer.getType().equals("REGULAR")) {
            return 0.0;  // без скидки
        } else if (customer.getType().equals("PREMIUM")) {
            return 0.1;  // 10% скидка
        } else if (customer.getType().equals("VIP")) {
            return 0.2;  // 20% скидка
        }
        // ❌ При добавлении нового типа клиента нужно МЕНЯТЬ этот код!
        return 0.0;
    }
}

// ❌ ЕЩЁ ОДИН ПРИМЕР НАРУШЕНИЯ
public class ShapeDrawer {
    
    public void draw(Object shape) {
        if (shape instanceof Circle) {
            drawCircle((Circle) shape);
        } else if (shape instanceof Rectangle) {
            drawRectangle((Rectangle) shape);
        } else if (shape instanceof Triangle) {
            drawTriangle((Triangle) shape);
        }
        // ❌ При добавлении новой фигуры нужно МЕНЯТЬ этот код!
    }
    
    private void drawCircle(Circle circle) { /* ... */ }
    private void drawRectangle(Rectangle rect) { /* ... */ }
    private void drawTriangle(Triangle triangle) { /* ... */ }
}

// Проблемы:
// • Каждое добавление нового типа требует изменения кода
// • Риск сломать существующую функциональность
// • Нарушение SRP (класс отвечает за все типы)
// • Сложно тестировать
```

```java
// ✅ ИНТЕРФЕЙС (абстракция)
public interface DiscountStrategy {
    double calculateDiscount(double amount);
}

// ✅ КОНКРЕТНЫЕ СТРАТЕГИИ
public class RegularDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double amount) {
        return 0.0;  // без скидки
    }
}

public class PremiumDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double amount) {
        return amount * 0.1;  // 10% скидка
    }
}

public class VIPDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double amount) {
        return amount * 0.2;  // 20% скидка
    }
}

// ✅ НОВЫЙ ТИП БЕЗ ИЗМЕНЕНИЯ СУЩЕСТВУЮЩЕГО КОДА
public class GoldDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double amount) {
        return amount * 0.3;  // 30% скидка
    }
}

// ✅ КЛАСС, КОТОРЫЙ ИСПОЛЬЗУЕТ СТРАТЕГИЮ
public class Customer {
    private DiscountStrategy discountStrategy;
    
    public Customer(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }
    
    public double calculateFinalPrice(double amount) {
        double discount = discountStrategy.calculateDiscount(amount);
        return amount - discount;
    }
}

// ✅ ИСПОЛЬЗОВАНИЕ
Customer regularCustomer = new Customer(new RegularDiscount());
Customer premiumCustomer = new Customer(new PremiumDiscount());
Customer vipCustomer = new Customer(new VIPDiscount());
Customer goldCustomer = new Customer(new GoldDiscount());  // ✅ новый тип!

System.out.println(regularCustomer.calculateFinalPrice(100));  // 100.0
System.out.println(premiumCustomer.calculateFinalPrice(100));  // 90.0
System.out.println(vipCustomer.calculateFinalPrice(100));      // 80.0
System.out.println(goldCustomer.calculateFinalPrice(100));     // 70.0
```

## L — Liskov Substitution Principle (LSP)
![img.png](/src/main/resources/images/liskov.png)

```java
// ❌ КЛАССИЧЕСКИЙ ПРИМЕР НАРУШЕНИЯ
public class Rectangle {
    protected int width;
    protected int height;
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getArea() {
        return width * height;
    }
}

// ❌ НАРУШЕНИЕ LSP!
public class Square extends Rectangle {
    
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // ❌ Меняем и height!
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;  // ❌ Меняем и width!
        this.height = height;
    }
}

// ❌ ПРОБЛЕМА ПРИ ИСПОЛЬЗОВАНИИ
public class Test {
    public static void testRectangle(Rectangle rectangle) {
        rectangle.setWidth(5);
        rectangle.setHeight(4);
        
        // Ожидание: area = 5 * 4 = 20
        if (rectangle.getArea() != 20) {
            throw new RuntimeException("Ожидается площадь 20!");
        }
    }
    
    public static void main(String[] args) {
        Rectangle rectangle = new Rectangle();
        testRectangle(rectangle);  // ✅ OK
        
        Rectangle square = new Square();
        testRectangle(square);  // ❌ Exception! Площадь = 16, а не 20
    }
}

// Почему это нарушение LSP?
// • Square не может полностью заменить Rectangle
// • Поведение Square отличается от Rectangle
// • Код, работающий с Rectangle, ломается с Square
```

```java
// ✅ ПРАВИЛЬНОЕ НАСЛЕДОВАНИЕ
public abstract class Shape {
    public abstract int getArea();
}

public class Rectangle extends Shape {
    private int width;
    private int height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    @Override
    public int getArea() {
        return width * height;
    }
}

public class Square extends Shape {
    private int side;
    
    public Square(int side) {
        this.side = side;
    }
    
    public void setSide(int side) {
        this.side = side;
    }
    
    @Override
    public int getArea() {
        return side * side;
    }
}

// ✅ ТЕПЕРЬ LSP СОБЛЮДАЕТСЯ
public class Test {
    public static void testShape(Shape shape) {
        int area = shape.getArea();
        System.out.println("Площадь: " + area);
    }
    
    public static void main(String[] args) {
        Shape rectangle = new Rectangle(5, 4);
        testShape(rectangle);  // ✅ OK, площадь = 20
        
        Shape square = new Square(5);
        testShape(square);  // ✅ OK, площадь = 25
        
        // Оба подкласса корректно заменяют Shape
    }
}
```
##  I — Interface Segregation Principle (ISP)

![img.png](/src/main/resources/images/is.png)

```java
// ❌ ОДИН БОЛЬШОЙ ИНТЕРФЕЙС
public interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void writeReport();
}

// ❌ РОБОТ НЕ ЕСТ И НЕ СПИТ!
public class Robot implements Worker {
    @Override
    public void work() {
        System.out.println("Робот работает");
    }
    
    @Override
    public void eat() {
        throw new UnsupportedOperationException("Робот не ест!");
    }
    
    @Override
    public void sleep() {
        throw new UnsupportedOperationException("Робот не спит!");
    }
    
    @Override
    public void attendMeeting() {
        throw new UnsupportedOperationException("Робот не ходит на собрания!");
    }
    
    @Override
    public void writeReport() {
        System.out.println("Робот пишет отчет");
    }
}

// ❌ ПРОБЛЕМЫ:
// • Robot вынужден реализовывать ненужные методы
// • Много UnsupportedOperationException
// • Интерфейс слишком большой
// • Сложно понять, что должен делать класс
```
```java
// ✅ МАЛЕНЬКИЕ СПЕЦИАЛИЗИРОВАННЫЕ ИНТЕРФЕЙСЫ
public interface Workable {
    void work();
}

public interface Feedable {
    void eat();
}

public interface Sleepable {
    void sleep();
}

public interface MeetingAttendable {
    void attendMeeting();
}

public interface ReportWritable {
    void writeReport();
}

// ✅ ЧЕЛОВЕК РЕАЛИЗУЕТ ТОЛЬКО НУЖНЫЕ ИНТЕРФЕЙСЫ
public class Human implements Workable, Feedable, Sleepable, 
                               MeetingAttendable, ReportWritable {
    @Override
    public void work() {
        System.out.println("Человек работает");
    }
    
    @Override
    public void eat() {
        System.out.println("Человек ест");
    }
    
    @Override
    public void sleep() {
        System.out.println("Человек спит");
    }
    
    @Override
    public void attendMeeting() {
        System.out.println("Человек на собрании");
    }
    
    @Override
    public void writeReport() {
        System.out.println("Человек пишет отчет");
    }
}

// ✅ РОБОТ РЕАЛИЗУЕТ ТОЛЬКО ТО, ЧТО НУЖНО
public class Robot implements Workable, ReportWritable {
    @Override
    public void work() {
        System.out.println("Робот работает");
    }
    
    @Override
    public void writeReport() {
        System.out.println("Робот пишет отчет");
    }
}

// ✅ ИСПОЛЬЗОВАНИЕ
public class Office {
    public void assignWork(Workable worker) {
        worker.work();  // ✅ Работает и с Human, и с Robot
    }
    
    public void scheduleLunch(Feedable entity) {
        entity.eat();  // ✅ Только для тех, кто ест
    }
}

// ✅ ИСПОЛЬЗОВАНИЕ
Office office = new Office();

Human human = new Human();
Robot robot = new Robot();

office.assignWork(human);  // ✅ OK
office.assignWork(robot);  // ✅ OK

office.scheduleLunch(human);  // ✅ OK
// office.scheduleLunch(robot);  // ❌ Compile error! Robot не Feedable
```

## D — Dependency Inversion Principle (DIP)

![img.png](/src/main/resources/images/dependencyInversion.png)

```java
// ❌ КЛАСС ВЕРХНЕГО УРОВНЯ ЗАВИСИТ ОТ КЛАССА НИЖНЕГО УРОВНЯ
public class MySQLDatabase {
    public void connect() {
        System.out.println("Подключение к MySQL");
    }
    
    public void executeQuery(String query) {
        System.out.println("Выполнение запроса: " + query);
    }
}

// ❌ НАРУШЕНИЕ DIP!
public class UserService {
    private MySQLDatabase database;  // ❌ Зависимость от конкретной реализации!
    
    public UserService() {
        this.database = new MySQLDatabase();
    }
    
    public void getUser(String id) {
        database.connect();
        database.executeQuery("SELECT * FROM users WHERE id = " + id);
    }
}

// Проблемы:
// • UserService жестко привязан к MySQLDatabase
// • Невозможно использовать PostgreSQL без изменения UserService
// • Сложно тестировать (нужна реальная БД)
// • Невозможно замокать для unit-тестов
```
```java
// ✅ АБСТРАКЦИЯ (интерфейс)
public interface Database {
    void connect();
    void executeQuery(String query);
}

// ✅ ДЕТАЛИ РЕАЛИЗАЦИИ (зависят от абстракции)
public class MySQLDatabase implements Database {
    @Override
    public void connect() {
        System.out.println("Подключение к MySQL");
    }
    
    @Override
    public void executeQuery(String query) {
        System.out.println("MySQL: " + query);
    }
}

public class PostgreSQLDatabase implements Database {
    @Override
    public void connect() {
        System.out.println("Подключение к PostgreSQL");
    }
    
    @Override
    public void executeQuery(String query) {
        System.out.println("PostgreSQL: " + query);
    }
}

public class InMemoryDatabase implements Database {
    @Override
    public void connect() {
        System.out.println("Подключение к InMemory DB");
    }
    
    @Override
    public void executeQuery(String query) {
        System.out.println("InMemory: " + query);
    }
}

// ✅ КЛАСС ВЕРХНЕГО УРОВНЯ ЗАВИСИТ ОТ АБСТРАКЦИИ
public class UserService {
    private Database database;  // ✅ Зависимость от интерфейса!
    
    // ✅ Dependency Injection через конструктор
    public UserService(Database database) {
        this.database = database;
    }
    
    public void getUser(String id) {
        database.connect();
        database.executeQuery("SELECT * FROM users WHERE id = " + id);
    }
}

// ✅ ИСПОЛЬЗОВАНИЕ
// Production
Database mysql = new MySQLDatabase();
UserService userService = new UserService(mysql);
userService.getUser("123");

// Можно легко переключиться на PostgreSQL
Database postgres = new PostgreSQLDatabase();
UserService userService2 = new UserService(postgres);
userService2.getUser("123");

// Тестирование с моком
Database mockDb = new InMemoryDatabase();
UserService testService = new UserService(mockDb);
testService.getUser("123");
```