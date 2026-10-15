## WeakHashMap
это реализация Map, в которой ключи хранятся через слабые ссылки (WeakReference). Это значит, что если на ключ нет сильных ссылок извне, сборщик мусора (GC) может удалить его, и запись автоматически исчезнет из Map.

```java
WeakHashMap<Object, String> map = new WeakHashMap<>();

Object key1 = new Object();  // Сильная ссылка на key1
Object key2 = new Object();  // Сильная ссылка на key2

map.put(key1, "value1");
map.put(key2, "value2");

System.out.println(map.size());  // 2

// Убираем сильную ссылку на key1
key1 = null;

// Запускаем сборку мусора
System.gc();

// Даём GC время поработать
Thread.sleep(100);

System.out.println(map.size());  // 1! Запись с key1 удалена автоматически
System.out.println(map.get(key2));  // "value2" — key2 ещё жив
```
**Для чего используется**:
* Кэш, который не вызывает утечек памяти
* Хранение метаданных для объектов

**Важные нюансы**
* Литералы нельзя использовать из за того, что их не чистит gc
* не потокобезопасен
* Удаление только при следующей работе gc