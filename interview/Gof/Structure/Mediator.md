**Определение**:   
Определяет объект, инкапсулирующий взаимодействие множества объектов. Mediator ослабляет связанность, избавляя объекты от необходимости явно ссылаться друг на друга.

**Проблема, которую решает**:  
Представьте систему, где 10 компонентов должны взаимодействовать друг с другом. Если каждый компонент знает о каждом другом, получается "спагетти-код" с кучей перекрестных связей.  
**Решение**:  
Вместо прямого общения компоненты общаются через единого посредника (медиатора).

## Mediator в Spring

**Сценарий**: Несколько сервисов должны взаимодействовать друг с другом, но мы не хотим, чтобы они знали друг о друге.

```java
// Медиатор координирует взаимодействие
@Service
public class OrderMediator { // Это МЕДИАТОР
    
    @Autowired private InventoryService inventory;
    @Autowired private PaymentService payment;
    @Autowired private NotificationService notification;
    
    // Координируем взаимодействие между компонентами
    public void handlePaymentSuccess(Payment payment) {
        // Компоненты не знают друг о друге, они общаются через медиатор
        inventory.updateStock(payment.getItems());
        notification.sendConfirmation(payment.getUser());
        // payment не знает о inventory и notification
        // inventory не знает о payment и notification
    }
}

@Service
public class PaymentService {
    @Autowired private OrderMediator mediator;
    
    public void processPayment(Payment payment) {
        // Обрабатываем платеж...
        
        // Вместо прямого вызова inventory или notification,
        // обращаемся к медиатору
        mediator.handlePaymentSuccess(payment);
    }
}

@Service
public class InventoryService {
    @Autowired private OrderMediator mediator;
    
    public void updateStock(List<Item> items) {
        // Обновляем库存...
        
        // Если нужно уведомить другие компоненты, обращаемся к медиатору
        mediator.handleStockUpdated(items);
    }
}
```