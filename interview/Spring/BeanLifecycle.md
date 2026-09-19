## Bean Lifecycle

Это последовательность этапов, которые проходит бин от создания до уничтожения в Spring IoC контейнере.  

Основные фазы:
1. Создание (Instantiation)  
2. Заполнение зависимостей (Populate properties) 
3. Вызов callback-методов инициализации
4. Использование бина  
5. Вызов callback-методов уничтожения

##  ЖИЗНЕННЫЙ ЦИКЛ:
### 1. BeanDefinition загрузка
Чтение @Component, @Bean, XML  
Создание BeanDefinition объектов  

### 2. BeanFactoryPostProcessor
Модификация BeanDefinition  
Работает c метаданными  
Вызывается ДО создания бинов  

### 3. Создание бина (Instantiation)
Вызов конструктора  
Raw bean object создан  

### 4. Заполнение зависимостей (Dependency Injection)  
@Autowired, @Value  
Setter injection  

### 5. Вызов Aware-методов
Это способ получить доступ к инфраструктуре Spring БЕЗ явного внедрения через @Autowired.  
Spring вызывает setter-методы при инициализации бина

### 6. BeanPostProcessor.postProcessBeforeInitialization()
BeanPostProcessor — интерфейс для кастомной модификации бинов
на стадиях before и after initialization.   

### 7. Init-методы
@PostConstruct 

### 8. BeanPostProcessor.postProcessAfterInitialization()
• Создания AOP-прокси  
• Обработки @Async 

### 9. Бин готов к использованию, после вызываются @PreDestroy 