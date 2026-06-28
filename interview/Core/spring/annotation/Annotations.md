## @RestController — это композиция аннотаций @Controller и @ResponseBody.

## @Controller:  
• Возвращает имя View (шаблона)  
• Использует ViewResolver для рендеринга HTML  
• Подходит для веб-приложений с серверным рендерингом

## @RestController:  
• Возвращает объект, который конвертируется в JSON/XML  
• Использует HttpMessageConverter (Jackson)  
• Подходит для REST API и микросервисов  
• Все методы автоматически имеют @ResponseBody

## @Component 
- Базовая регистрация бина без доп. поведения  
## @Service 
- Нет встроенного поведения (по умолчанию), маркер для AOP  
## @Repository 
- Автоматическая трансляция исключений в DataAccessException,  Автоматически подключает PersistenceExceptionTranslationPostProcessor, который конвертирует persistence-исключения в DataAccessException. Это позволяет писать слой сервисов, не зависящий от конкретной ORM/БД  

## **PersistenceExceptionTranslationPostProcessor** - это

- BeanPostProcessor, который автоматически регистрируется при наличии @Repository в контексте
- Создаёт AOP-прокси для всех @Repository бинов
- Перехватывает persistence-исключения и конвертирует их в DataAccessException
- Делает код независимым от конкретной ORM/БД

## @Transactional  
— это AOP-прокси, который оборачивает вызовы метода в транзакцию.
- По умолчанию rollback только на RuntimeException и Error. Checked exception НЕ вызовет rollback

## PROPAGATION — поведение транзакции при вложенных вызовах

ТИПЫ PROPAGATION

###   REQUIRED   (по умолчанию) → join или create

• Если есть существующая транзакция → присоединиться  
• Если нет → создать новую

###  SUPPORTS                  → join или non-transactional
• Если есть транзакция → присоединиться  
• Если нет → работать БЕЗ транзакции

###  MANDATORY                 → join или exception
• Если есть транзакция → присоединиться  
• Если нет → БРОСИТЬ ИСКЛЮЧЕНИЕ!

###  REQUIRES_NEW              → всегда create (suspend current)
• ВСЕГДА создаёт НОВУЮ транзакцию  
• Текущая транзакция ПРИОСТАНАВЛИВАЕТСЯ (suspend)

###  NOT_SUPPORTED             → suspend current, non-transactional
• Если есть транзакция → ПРИОСТАНАВЛИВАЕТСЯ  
• Работает БЕЗ транзакции

###  NEVER                     → non-transactional или exception
• Если есть транзакция → БРОСИТЬ ИСКЛЮЧЕНИЕ!  
• Если нет → работать без транзакции

###  NESTED                    → nested (savepoint) или create
• Если есть транзакция → создать SAVEPOINT (вложенная tx)  
• Если нет → создать новую (как REQUIRED)  
Отличие от REQUIRES_NEW:  
• NESTED — это часть outer транзакции  
• Если outer откатится → nested тоже откатится  
• Если nested откатится → outer может продолжить  
Требует поддержки savepoints от JDBC driver!

### Как работает @Transactional(readOnly = true)
1. При flush() не делает dirty checking
2. Не генерирует UPDATE-запросы
3. БД может применить оптимизации (например, не брать блокировки)
4. Spring НЕ вызывает setAutoCommit(false) — экономия ресурсов
5. Если попытаться сделать save() — будет исключение

Использовать для методов, которые только читают данные.

![img.png](/src/main/resources/images/annotation.png)

**Что делает Spring при старте приложения:**  

![img.png](/src/main/resources/images/Annotations2.png)

## Типы прокси: JDK vs CGLIB
JDK (JDK Dynamic Proxy) 
- Работает только с ИНТЕРФЕЙСАМИ
- Создаёт класс в runtime, реализующий интерфейс
- Быстрее создаётся, медленнее вызывается
- Используется по умолчанию, если класс реализует интерфейс

CGLIB Proxy
- Работает с КЛАССАМИ (наследует от них)
- Генерирует подкласс в runtime
- Медленнее создаётся, быстрее вызывается
- Используется, если класс НЕ реализует интерфейс
- НЕ МОЖЕТ проксировать final классы/методы!
