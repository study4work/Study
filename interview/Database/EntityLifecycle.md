В Hibernate/JPA сущность (entity) может находиться в одном из четырех состояний. Эти состояния определяют, связана ли сущность с EntityManager (или Session в старом Hibernate API) и синхронизирована ли она с базой данных.

## 1. Transient (Преходящее/Новое состояние)

Что это: Сущность создана через new, но еще не связана с EntityManager. Она существует только в памяти JVM. Hibernate о ней ничего не знает.  

Аналогия: Ты написал письмо, но еще не положил его в почтовый ящик. Почта (БД) о нем не знает.

```java
User user = new User();
user.setName("John");
user.setEmail("john@example.com");

// user находится в состоянии TRANSIENT
// В БД этой записи еще нет
// EntityManager о ней не знает
```

Что можно сделать:  
Изменять поля — это просто Java-объект  
Передать в метод, сериализовать, передать по сети  
Но изменения НЕ попадут в БД, пока не сделаем persist() или save()  

## 2. Managed / Persistent (Управляемое/Персистентное состояние)

Что это: Сущность связана с EntityManager и имеет представление в БД (или будет иметь после коммита транзакции). Hibernate отслеживает все изменения этой сущности (dirty checking) и автоматически синхронизирует их с БД при коммите транзакции.

Аналогия: Письмо лежит в почтовом ящике, и почтальон знает о нем. Любые изменения в письме будут учтены при доставке.

```java
@Transactional
public void createUser() {
    User user = new User();
    user.setName("John");
    
    entityManager.persist(user); 
    // Теперь user в состоянии MANAGED
    // Hibernate сгенерировал INSERT, но еще не выполнил (или уже выполнил, зависит от flush mode)
    
    user.setName("Jane"); 
    // Изменение отслеживается! При коммите транзакции Hibernate сделает UPDATE
}
```

Ключевые особенности:  
>entityManager.contains(user) вернет true  
Любые изменения автоматически синхронизируются с БД (dirty checking)  
Работает lazy loading для ассоциаций  
Сущность привязана к конкретному EntityManager (и, соответственно, к Session и транзакции)

Как попасть в это состояние:  
>entityManager.persist(entity) — для новых сущностей  
entityManager.merge(entity) — для detached сущностей  
entityManager.find(User.class, id) — загрузка из БД  
JPQL/Criteria запросы  
Lazy loading ассоциаций  

## 3. Detached (Отсоединенное состояние)

Что это: Сущность была связана с EntityManager, но EntityManager был закрыт (или сущность была явно отсоединена через detach() или clear()). Сущность все еще существует в памяти, но Hibernate больше не отслеживает ее изменения.

Аналогия: Письмо забрали из почтового ящика. Оно существует, но почтальон больше о нем не знает. Изменения в письме не будут доставлены.

```java
User user;

@Transactional
public User loadUser() {
    user = entityManager.find(User.class, 1L);
    // user в состоянии MANAGED
    return user;
}

public void updateUser() {
    // Транзакция уже закрыта, EntityManager закрыт
    // user теперь в состоянии DETACHED

    user.setName("Updated Name");
    // Изменение НЕ попадет в БД!
    // Hibernate больше не отслеживает этот объект

    // Чтобы сохранить изменения, нужно:
    // 1. Снова загрузить сущность из БД
    // 2. Или использовать merge()
}
```

Типичные сценарии, когда сущность становится detached:  
Закрытие EntityManager (например, выход из @Transactional метода в Spring)  
Явный вызов entityManager.detach(user)  
Явный вызов entityManager.clear() (отсоединяет ВСЕ сущности)  
Сериализация/десериализация (например, передача по REST API) 
Кеширование второго уровня (L2 cache)

```java
// LazyInitializationException - классика!
User user = loadUser(); // EntityManager закрыт
String cityName = user.getAddress().getCity(); 
// ОШИБКА! Address не загружен, а EntityManager закрыт, lazy loading невозможен

// Решение 1: Использовать JOIN FETCH в запросе
@Query("SELECT u FROM User u JOIN FETCH u.address WHERE u.id = :id")
User findUserWithAddress(@Param("id") Long id);

// Решение 2: Использовать Open Session in View (антипаттерн!)
// Решение 3: Использовать DTO вместо сущностей
```

Как вернуть в managed состояние:
```java
// Вариант 1: merge() - создает НОВУЮ managed копию
User managedUser = entityManager.merge(detachedUser);
managedUser.setName("New Name"); // Изменения попадут в БД

// Вариант 2: persist() - только для новых сущностей с тем же ID
// НЕ БУДЕТ РАБОТАТЬ для detached!

// Вариант 3: update() (в старом Hibernate API)
session.update(detachedUser);
```

Важное отличие merge() vs update():  
>merge() — создает новую managed сущность (или обновляет существующую в persistence context). Оригинальный detached объект остается detached.  
update() (Hibernate-specific) — делает тот же объект managed. Оригинальный объект становится managed.
>

## 4. Removed (Удаленное состояние)
Что это: Сущность была в managed состоянии, но был вызван remove(). Сущность помечена на удаление, но DELETE еще не выполнен (выполнится при flush/commit).  
Аналогия: Письмо помечено как "уничтожить", но еще не сожжено. 
```java
@Transactional
public void deleteUser(Long id) {
    User user = entityManager.find(User.class, id);
    // user в состоянии MANAGED
    
    entityManager.remove(user);
    // user в состоянии REMOVED
    // DELETE еще не выполнен
    
    user.setName("Changed"); 
    // Изменение НЕ попадет в БД, потому что сущность будет удалена
}
// При коммите транзакции выполнится DELETE FROM users WHERE id = ?
```
После коммита транзакции сущность переходит в состояние transient (или остается removed, зависит от реализации).

## Persistence Context (Контекст персистентности)
Это ключевая концепция, связанная с managed состоянием.
Что это: Кэш первого уровня (L1 cache), который хранит все managed сущности в рамках одного EntityManager (или Session).

```java
@Transactional
public void demonstratePersistenceContext() {
    User user1 = entityManager.find(User.class, 1L);
    // Hibernate сделал SELECT, сохранил user1 в persistence context
    
    User user2 = entityManager.find(User.class, 1L);
    // Hibernate НЕ делал SELECT!
    // Он вернул тот же объект из persistence context
    
    System.out.println(user1 == user2); // true! Это один и тот же объект в памяти
}
```

Зачем это нужно:  
Идентичность: Гарантирует, что в рамках одной транзакции одна и та же сущность   представлена одним объектом.  
Производительность: Избегает повторных запросов к БД.  
Dirty checking: Позволяет отслеживать изменения.  

Проблемы:
```java
@Transactional
public void loadManyUsers() {
    // Загрузили 10 000 пользователей
    List<User> users = entityManager.createQuery("SELECT u FROM User u").getResultList();
    // Все 10 000 сущностей в persistence context
    // Это занимает много памяти!
    
    // Решение: использовать pagination или stateless session
}
```
