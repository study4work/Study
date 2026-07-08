![img.png](/src/main/resources/images/kafka1.png)
![img.png](/src/main/resources/images/kafka2.png)  
## ОСНОВНЫЕ ПОНЯТИЯ 

Topic (Топик) - Логическая категория сообщений (как "таблица" в БД) Пример: "orders", "payments", "user-events"

Partition (Партиция) - Topic делится на партиции для параллелизма, Каждая партиция — упорядоченный лог сообщений, Сообщения в партиции имеют уникальный offset

Offset - Уникальный идентификатор сообщения в партиции  

Consumer Group - Группа консьюмеров, которые вместе читают topic, Каждая партиция достаётся ТОЛЬКО ОДНОМУ консьюмеру в группе

Broker - Сервер Kafka, хранящий партиции, Кластер = несколько брокеров

Leader / Replica - Каждая партиция имеет 1 Leader и N Replicas
Leader обрабатывает чтение/запись, Replicas — копии для отказоустойчивости

ZooKeeper / KRaft - Хранит метаданные кластера, В новых версиях Kafka — KRaft (без ZooKeeper)

### ПРОЦЕСС ЗАПИСИ СООБЩЕНИЯ  
                                                       
1. Producer отправляет сообщение в Topic  
     ↓                                                 
2. Kafka определяет партицию:  
   • Если указан key → hash(key) % partitions_count    
   • Если key нет → round-robin
     ↓                                                 
3. Сообщение отправляется Leader партиции  
     ↓                                                 
4. Leader записывает сообщение в log (append-only)     
     ↓                                                 
5. Leader реплицирует на все In-Sync Replicas (ISR)    
     ↓                                                 
6. Когда все ISR подтвердили → Producer получает ACK   
     ↓                                                 
7. Consumer читает сообщение по offset         


### ТРИ ТИПА ГАРАНТИЙ ДОСТАВКИ
                                        
1. AT MOST ONCE (не более одного раза)  
   • Сообщения могут теряться  
   • Дубликатов нет  
   • Самая быстрая, но ненадёжная       
![img.png](/src/main/resources/images/kafka3.png)

2. AT LEAST ONCE (хотя бы один раз)  
   • Сообщения НЕ теряются  
   • Возможны дубликаты  
   • По умолчанию в Kafka            
![img.png](/src/main/resources/images/kafka4.png)

3. EXACTLY ONCE (ровно один раз)  
   • Сообщения НЕ теряются  
   • Дубликатов нет  
   • Самая сложная, требует транзакций  
![img.png](/src/main/resources/images/kafka5.png)

