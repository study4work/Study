## Decorator (Декоратор)

Суть: Динамически добавляет новую функциональность объекту.

Как это выглядит в Spring:
Это один из самых важных паттернов в Spring. Он реализуется через AOP (Aspect-Oriented Programming) и Прокси-объекты.  
**Пример**: У вас есть @Service UserService, который просто сохраняет пользователя в базу данных. Но бизнес требует: перед сохранением нужно проверить права доступа, после сохранения — отправить событие, а сам метод должен быть транзакционным.
Вы не пишете этот код внутри UserService. Вы просто вешаете аннотации @Transactional, @PreAuthorize и @EventListener.
Что делает Spring: Он берет ваш оригинальный бин и «оборачивает» (декорирует) его в прокси-объект. Когда контроллер вызывает userService.save(), он на самом деле обращается к прокси. Прокси сначала открывает транзакцию (Декоратор 1), потом проверяет права (Декоратор 2), потом вызывает ваш реальный код, а потом коммитит транзакцию. Ваш исходный код остается чистым!

```java
// Компонент
public interface Coffee {
    String getDescription();
    double getCost();
}

// Конкретный компонент
public class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Простой кофе";
    }
    
    @Override
    public double getCost() {
        return 5.0;
    }
}

// Декоратор
public abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost();
    }
}

// Конкретные декораторы
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " + молоко";
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 2.0;
    }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " + сахар";
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 0.5;
    }
}

public class WhipDecorator extends CoffeeDecorator {
    public WhipDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + " + взбитые сливки";
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 1.5;
    }
}

// Использование:
Coffee coffee = new SimpleCoffee();
System.out.println(coffee.getDescription() + " = $" + coffee.getCost());
// Простой кофе = $5.0

coffee = new MilkDecorator(coffee);
System.out.println(coffee.getDescription() + " = $" + coffee.getCost());
// Простой кофе + молоко = $7.0

coffee = new SugarDecorator(coffee);
System.out.println(coffee.getDescription() + " = $" + coffee.getCost());
// Простой кофе + молоко + сахар = $7.5

coffee = new WhipDecorator(coffee);
System.out.println(coffee.getDescription() + " = $" + coffee.getCost());
// Простой кофе + молоко + сахар + взбитые сливки = $9.0
```