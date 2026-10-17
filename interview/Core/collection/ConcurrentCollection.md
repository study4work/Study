## ConcurrentHashMap

• synchronized на уровне bucket (узла)  
• CAS для вставки в пустой bucket  
• Если bucket пуст — вставляем без synchronized  
• fail-safe поведение

## CopyOnWriteArrayList  
• При ЛЮБОЙ модификации создаётся КОПИЯ массива  
• Чтение работает без блокировок   
• Запись — с блокировкой (ReentrantLock)  
• fail-safe поведение

## HASHTABLE  

• Устарел (JDK 1.0, 1996)  
• synchronized на всех методах, блокировка на самом объекте Hashtable  
Только ОДИН поток может выполнять любой метод в момент времени  
• Блокирует ВСЮ таблицу  
• Низкая конкурентность