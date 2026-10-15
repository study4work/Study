**AutoCloseable** — это интерфейс, введённый в Java 7 специально для try-with-resources. Определяет объект, который нужно закрывать после использования.  
```java
package java.lang;

public interface AutoCloseable {
    void close() throws Exception;
    //Метод close() может бросать ЛЮБОЕ исключение (throws Exception)
}
```
## Разница между AutoCloseable и Closeable
Оба интерфейса служат для закрытия ресурсов, Closeable наследует от AutoCloseable, а не наоборот! Это сделано для обратной совместимости.
```java
// Closeable — только IOException
public interface Closeable extends AutoCloseable {
    @Override
    public void close() throws IOException;  // ← Только IOException!
}

// AutoCloseable — любое Exception
public interface AutoCloseable {
    void close() throws Exception;  // ← Любое Exception
}
```
**Используйте Closeable, когда**:  
Работаете с I/O ресурсами (файлы, потоки)  
Ошибки закрытия — это IOException   

**Реализуйте AutoCloseable, когда**:  
Создаёте класс, требующий очистки ресурсов  
Ошибки закрытия могут быть разными (не только I/O)
