## ConcurrentHashMap

• synchronized на уровне bucket (узла)  
• CAS для вставки в пустой bucket  
• Если bucket пуст — вставляем без synchronized  
• Если bucket не пустой → synchronized на головном узле bucket.  
• fail-safe поведение  
• get() — полностью lock-free. Использует volatile для чтения, что гарантирует видимость.

```java
// ✅ Правильно (атомарно)
map.computeIfAbsent("key", k -> expensiveComputation());
map.merge("counter", 1, Integer::sum);
map.replace("key", oldValue, newValue);

// ❌ Неправильно (race condition!)
if (!map.containsKey("key")) {
    map.put("key", value);  // check-then-act!
}
```
Составные операции атомарны.

## CopyOnWriteArrayList
```java
Исходное состояние:
array = [A, B, C]  ← на эту ссылку смотрят все читатели

Добавляем "D":
1. Создаем копию: [A, B, C, D]
2. Атомарно меняем ссылку: array = [A, B, C, D]
3. Старый массив [A, B, C] остается в памяти, пока его читают

Теперь:
- Новые читатели видят [A, B, C, D]
- Старые читатели (если они уже начали итерацию) видят [A, B, C]
```
• При ЛЮБОЙ модификации создаётся КОПИЯ массива  
• Чтение работает без блокировок   
• Запись — с блокировкой (ReentrantLock)  
• fail-safe поведение итератор в виде ссылки

Не атомарны составные операции
```java
if (!list.contains(x)) 
    list.add(x)
```
 — это race condition. Нужна внешняя синхронизация.
## HASHTABLE  

• synchronized на всех методах, блокировка на самом объекте Hashtable  
Только ОДИН поток может выполнять любой метод в момент времени  
• Блокирует ВСЮ таблицу  
• Низкая конкурентность