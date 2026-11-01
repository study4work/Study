## Deadlock
Взаимоблокировка иллюстрируется классической проблемой «обедающих философов»: пять философов сидят за круглым столом, между каждой парой лежит одна палочка, а для еды нужны две. Если каждый схватит палочку слева и будет ждать палочку справа — никто не поест.

Есть несколько причин дедлоков
1. Взаимоблокировки, вызванные порядком наложения блокировок
```java
// Warning: deadlock-prone!
public class LeftRightDeadlock {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftRight() {
        synchronized (left) {
            synchronized (right) {
                doSomething();
            }
        }
    }

    public void rightLeft() {
        synchronized (right) {
            synchronized (left) {
                doSomethingElse();
            }
        }
    }
}
```

2. Взаимоблокировки, вызванные динамическим порядком блокировок
```java
// Warning: deadlock-prone!
public void transferMoney(Account fromAccount, Account toAccount, DollarAmount amount)
        throws InsufficientFundsException {
    synchronized (fromAccount) {
        synchronized (toAccount) {
            if (fromAccount.getBalance().compareTo(amount) < 0)
                throw new InsufficientFundsException();
            fromAccount.debit(amount);
            toAccount.credit(amount);
        }
    }
}
```
Порядок блокировок зависит от порядка аргументов. Два потока, вызывающих transferMoney(X, Y, ...) и transferMoney(Y, X, ...) одновременно, попадут во взаимоблокировку.
Решение — индуцировать порядок захвата блокировок. Один из способов — System.identityHashCode; при равенстве хэшей используется третья, «разрывающая связь» (tie) блокировка:
```java
private static final Object tieLock = new Object();

public void transferMoney(final Account fromAcct, final Account toAcct,
                          final DollarAmount amount) throws InsufficientFundsException {
    class Helper {
        public void transfer() throws InsufficientFundsException {
            if (fromAcct.getBalance().compareTo(amount) < 0)
                throw new InsufficientFundsException();
            fromAcct.debit(amount);
            toAcct.credit(amount);
        }
    }
    int fromHash = System.identityHashCode(fromAcct);
    int toHash = System.identityHashCode(toAcct);

    if (fromHash < toHash) {
        synchronized (fromAcct) { synchronized (toAcct) { new Helper().transfer(); } }
    } else if (fromHash > toHash) {
        synchronized (toAcct) { synchronized (fromAcct) { new Helper().transfer(); } }
    } else {
        synchronized (tieLock) {
            synchronized (fromAcct) { synchronized (toAcct) { new Helper().transfer(); } }
        }
    }
}
```
Если у Account есть уникальный неизменяемый сопоставимый ключ (например, номер счёта), упорядочивание ещё проще — по ключу, без tieLock.

3. (deadlock) из-за нарушения порядка захвата блокировок
```java
class Taxi {
    private Point location, destination;
    private final Dispatcher dispatcher;

    // Блокировка захватывается на уровне метода (монитор объекта Taxi)
    public synchronized Point getLocation() {
        return location;
    }

    // Блокировка захватывается на уровне метода (монитор объекта Taxi)
    public synchronized void setLocation(Point location) {
        this.location = location;
        if (location.equals(destination)) {
            // ПРОБЛЕМА: Вызов чужого метода (dispatcher) 
            // при УДЕРЖИВАЕМОЙ блокировке Taxi!
            dispatcher.notifyAvailable(this);
        }
    }
}

class Dispatcher {
    private final Set<Taxi> taxis;
    private final Set<Taxi> availableTaxis;

    public Dispatcher() {
        taxis = new HashSet<>();
        availableTaxis = new HashSet<>();
    }

    // Блокировка захватывается на уровне метода (монитор объекта Dispatcher)
    public synchronized void notifyAvailable(Taxi taxi) {
        availableTaxis.add(taxi);
    }

    // Блокировка захватывается на уровне метода (монитор объекта Dispatcher)
    public synchronized Image getImage() {
        Image image = new Image();
        for (Taxi taxi : taxis) {
            // ПРОБЛЕМА: Вызов чужого метода (taxi) 
            // при УДЕРЖИВАЕМОЙ блокировке Dispatcher!
            image.drawMarker(taxi.getLocation());
        }
        return image;
    }
}
```
Причина дедлока (Пошаговый сценарий катастрофы)  
Дедлок возникает из-за циклического ожидания. Для этого достаточно двух потоков, которые выполняются одновременно и пересекаются во времени определенным образом.  
Представь, что есть Поток А и Поток Б:  
1. Поток А вызывает taxi.setLocation(...).
* Он успешно захватывает блокировку объекта Taxi (входит в synchronized метод).
* Обновляет координаты.
* Пытается вызвать dispatcher.notifyAvailable(this). Для этого ему нужна блокировка объекта Dispatcher.
* Допустим, в этот момент блокировка Dispatcher уже занята, и Поток А переходит в состояние ожидания (BLOCKED).
2. Поток Б вызывает dispatcher.getImage().
* Он успешно захватывает блокировку объекта Dispatcher (входит в synchronized метод).
* Начинает перебирать такси в цикле.
* Пытается вызвать taxi.getLocation(). Для этого ему нужна блокировка объекта Taxi.
* Но блокировка Taxi всё ещё удерживается Потоком А! Поток Б переходит в состояние ожидания (BLOCKED).
Итог:  
* Поток А держит Taxi и ждёт Dispatcher.
* Поток Б держит Dispatcher и ждёт Taxi.
* Ни один из них не может освободить свою блокировку, потому что для этого нужно завершить метод, а завершить метод нельзя, не получив вторую блокировку. Классический дедлок.
```java
    public void setLocation(Point location) {
        boolean reachedDestination;
        synchronized (this) { // 1. Захватили блокировку Taxi
            this.location = location;
            reachedDestination = location.equals(destination);
        } // 2. ОСВОБОДИЛИ блокировку Taxi
        
        // 3. Открытый вызов (open call) вне блокировки!
        if (reachedDestination) {
            dispatcher.notifyAvailable(this); 
        }
    }
```
Теперь, если Поток А вызовет dispatcher.notifyAvailable, он уже не держит блокировку Taxi. Если в этот же момент Поток Б вызовет taxi.getLocation(), он спокойно получит блокировку Taxi, выполнит метод и отпустит её. Циклическая зависимость разорвана, потому что порядок захвата блокировок больше не пересекается.