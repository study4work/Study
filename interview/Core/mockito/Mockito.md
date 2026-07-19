## Mockito 
Mockito работает на уровне Unit-тестов. Его задача — заменить зависимости (collaborators) вашего тестируемого объекта (SUT - System Under Test) на симулякры, чтобы проверить логику внутри SUT в вакууме.

### Виды "заменителей" в Mockito

1. **Dummy (Манекен)** 

Объект, который передается в метод, но никогда не используется. Он нужен только для того, чтобы код скомпилировался или чтобы заполнить параметры.

Когда использовать: Когда метод принимает много параметров, но вас интересует только один из них.

```java
// Метод принимает 3 параметра, но нам важен только email
public void sendNotification(String email, String message, Priority priority) { ... }

// В тесте:
Priority dummyPriority = null; // Или любой объект, который не используется
String dummyMessage = "";
when(mockService.sendNotification("user@test.com", dummyMessage, dummyPriority))
    .thenReturn(true);
```

2. **Stub (Заглушка)**

Объект, который всегда возвращает заранее заготовленные данные. Он не запоминает, как с ним взаимодействовали. Его задача — предоставить данные для теста.

Когда использовать: Когда вам нужно, чтобы зависимость вернула конкретные данные, чтобы протестировать логику вашего класса.

```java
// Нам нужно, чтобы UserRepository вернул конкретного пользователя
UserRepository mockRepo = mock(UserRepository.class);
when(mockRepo.findById(1L)).thenReturn(Optional.of(new User("John")));

// Теперь тестируем сервис, который использует этот репозиторий
UserService service = new UserService(mockRepo);
User result = service.getUser(1L);

assertThat(result.getName()).isEqualTo("John");
```

3. **Spy (Шпион)**

это обертка над реальным объектом. У него есть вся реальная логика. По умолчанию Spy вызывает реальные методы, но вы можете выборочно переопределить (заstubить) некоторые из них.

```java
class OrderService {
    public BigDecimal calculateTotal(Order order) {
        // Реальная сложная логика
        return order.getItems().stream()
            .map(Item::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void processOrder(Order order) {
        BigDecimal total = calculateTotal(order);
        System.out.println("Processing order with total: " + total);
        // Отправка в базу, отправка email и т.д.
    }
}

// Тест
@Test
void testProcessOrder() {
    // Создаем РЕАЛЬНЫЙ объект
    OrderService realService = new OrderService();
    
    // Оборачиваем его в Spy
    OrderService spyService = spy(realService);
    
    // Создаем тестовые данные
    Order order = new Order(List.of(new Item("Book", new BigDecimal("10.00"))));
    
    // Вызываем метод - вызовется РЕАЛЬНЫЙ calculateTotal
    spyService.processOrder(order);
    
    // Проверяем, что calculateTotal был вызван
    verify(spyService).calculateTotal(order);
}
```
Ключевой момент: Как переопределить метод в Spy
doReturn().when() (ПРАВИЛЬНО для Spy)
```java
OrderService spyService = spy(new OrderService());

// Переопределяем calculateTotal
doReturn(new BigDecimal("999.99"))
    .when(spyService)
    .calculateTotal(any());
```

Когда использовать:
>Когда вы хотите протестировать часть реального объекта, но не можете (или не хотите) мокать всё.
Когда вам нужно проверить, что метод реального объекта был вызван с определенными параметрами.

4. **Fake (Фейк)**

Реальная работающая реализация, но упрощенная. Fake не подходит для продакшена (например, не масштабируется, не персистентен), но полностью функционален для тестов.  
Когда использовать: Когда вам нужна реальная логика, но вы не хотите зависеть от внешней инфраструктуры (БД, сеть).

```java
// Интерфейс
interface UserRepository {
    User findById(Long id);
    void save(User user);
}

// Fake-реализация для тестов (in-memory)
class InMemoryUserRepository implements UserRepository {
    private Map<Long, User> storage = new HashMap<>();
    
    @Override
    public User findById(Long id) {
        return storage.get(id);
    }
    
    @Override
    public void save(User user) {
        storage.put(user.getId(), user);
    }
}

// В тесте:
UserRepository fakeRepo = new InMemoryUserRepository();
UserService service = new UserService(fakeRepo);

service.createUser(new User(1L, "John"));
User result = service.getUser(1L);

assertThat(result.getName()).isEqualTo("John");
```

>В Mockito: Fake не существует. Mockito создает только моки и шпионы. Fake — это концепция, которую вы реализуете сами.  

>**Когда Fake лучше Mock:**  
Когда у вас сложная логика в зависимости (например, БД с транзакциями), и вы хотите проверить, что ваш код корректно с ней работает.  
Когда моков слишком много, и тест превращается в портянку.  
Gotcha: Fake должен вести себя точно так же, как продакшен-реализация. Если ваш InMemoryRepository не проверяет уникальность id, а реальный PostgresRepository проверяет — тест пройдет, а в проде будет ошибка.

5. **Mock (Собственно Mock)**

Объект, который запоминает, как с ним взаимодействовали. Mock позволяет делать verify() — проверять, что методы вызывались с определенными параметрами, определенное количество раз, в определенном порядке. 
Когда использовать: Когда вам важно не что вернул метод, а как с ним взаимодействовали (side-effects).

```java
// Нам важно не то, что вернул EmailSender, а то, что он БЫЛ вызван
EmailSender mockSender = mock(EmailSender.class);

UserService service = new UserService(mockSender);
service.registerUser(new User("John", "john@test.com"));

// Проверяем, что email БЫЛ отправлен
verify(mockSender, times(1)).sendEmail(eq("john@test.com"), contains("Welcome")); 
```


### Behavior Verification vs State Verification:
**State Verification** (через Stub + Assert): Вы проверяете состояние после вызова. "Вернулся ли правильный объект?", "Изменилось ли поле?".  
**Behavior Verification** (через Mock + Verify): Вы проверяете поведение. "Был ли вызван метод?", "Сколько раз?", "С какими параметрами?".

##### Когда что использовать:  

**State Verification**   
— когда метод что-то возвращает. Это предпочтительный способ, потому что тесты менее хрупкие (не зависят от внутренних деталей реализации).  
**Behavior Verification**   
— когда метод void (например, отправка сообщения в очередь) или когда важен side-effect (например, что лог точно был записан).