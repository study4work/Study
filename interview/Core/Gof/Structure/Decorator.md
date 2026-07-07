## Decorator (Декоратор)

Суть: Динамически добавляет новую функциональность объекту.

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