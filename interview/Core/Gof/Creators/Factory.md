### Factory Method (Фабричный метод)
Суть: Определяет интерфейс для создания объекта, но позволяет подклассам решить, какой класс инстанцировать.

```java
// Продукт
public interface Transport {
    void deliver();
}

// Конкретные продукты
public class Truck implements Transport {
    @Override
    public void deliver() {
        System.out.println("Доставка грузовиком");
    }
}

public class Ship implements Transport {
    @Override
    public void deliver() {
        System.out.println("Доставка кораблем");
    }
}

// Создатель
public abstract class Logistics {
    
    // Фабричный метод
    public abstract Transport createTransport();
    
    // Бизнес-логика
    public void planDelivery() {
        Transport transport = createTransport();
        transport.deliver();
    }
}

// Конкретные создатели
public class RoadLogistics extends Logistics {
    @Override
    public Transport createTransport() {
        return new Truck();
    }
}

public class SeaLogistics extends Logistics {
    @Override
    public Transport createTransport() {
        return new Ship();
    }
}

// Использование:
Logistics logistics = new RoadLogistics();
logistics.planDelivery();  // Доставка грузовиком

logistics = new SeaLogistics();
logistics.planDelivery();  // Доставка кораблем
```