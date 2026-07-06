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

## fail-fast и fail-safe итераторы

fail-fast (ArrayList, HashMap):  
• Бросают ConcurrentModificationException  
• Если коллекция изменена во время итерации   
• НЕ thread-safe 

fail-safe  
• НЕ бросают exception  
• Работают со snapshot или copy  
• Могут не видеть последние изменения  
• Thread-safe

## HASHTABLE  

• Устарел (JDK 1.0, 1996)  
• synchronized на всех методах, блокировка на самом объекте Hashtable  
Только ОДИН поток может выполнять любой метод в момент времени  
• Блокирует ВСЮ таблицу  
• Низкая конкурентность