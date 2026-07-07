## Strategy (Стратегия)

Суть: Определяет семейство алгоритмов и позволяет выбирать алгоритм во время выполнения.

```java
// Стратегия
public interface PaymentStrategy {
    void pay(int amount);
}

// Конкретные стратегии
public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    
    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    @Override
    public void pay(int amount) {
        System.out.println("Оплата $" + amount + " картой " + cardNumber);
    }
}

public class PayPalPayment implements PaymentStrategy {
    private String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public void pay(int amount) {
        System.out.println("Оплата $" + amount + " через PayPal (" + email + ")");
    }
}

public class CryptoPayment implements PaymentStrategy {
    private String walletAddress;
    
    public CryptoPayment(String walletAddress) {
        this.walletAddress = walletAddress;
    }
    
    @Override
    public void pay(int amount) {
        System.out.println("Оплата $" + amount + " криптовалютой (" + walletAddress + ")");
    }
}

// Контекст
public class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    
    public void checkout(int amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Выберите способ оплаты");
        }
        paymentStrategy.pay(amount);
    }
}

// Использование:
ShoppingCart cart = new ShoppingCart();

cart.setPaymentStrategy(new CreditCardPayment("1234-5678-9012-3456"));
cart.checkout(100);  // Оплата $100 картой 1234-5678-9012-3456

cart.setPaymentStrategy(new PayPalPayment("user@email.com"));
cart.checkout(200);  // Оплата $200 через PayPal (user@email.com)

cart.setPaymentStrategy(new CryptoPayment("0x123abc..."));
cart.checkout(300);  // Оплата $300 криптовалютой (0x123abc...)
```