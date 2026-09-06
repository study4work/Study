## AOT (Ahead-of-Time)

компиляция байт-кода в машинный код до запуска приложения, а не во время выполнения.

1. Исходный код (MyApp.java)  
   ↓  
   javac  
   ↓  
2. Байт-код (MyApp.class)  
   ↓  
   AOT-компилятор (GraalVM native-image)  
   ↓  
3. Машинный код (MyApp binary) - платформенно-зависимый  
   ↓  
4. Запуск: ./MyApp (без JVM!)

Ключевое отличие: Нет JVM в рантайме! Получается standalone binary, как если бы ты написал на C/C++.

GraalVM Native Image: Основная реализация AOT для Java  
GraalVM — это альтернативная JVM от Oracle, которая включает:  
Graal compiler — замена C2 compiler (более продвинутый JIT)  
native-image — инструмент для AOT-компиляции в standalone binary  

Как работает native-image:  
1. Собираешь обычный JAR  
mvn package

2. Компилируешь в native image  
native-image -jar target/myapp-1.0.jar  

3. Получаешь standalone binary  
./myapp-1.0

Что происходит "под капотом":  
Static analysis — анализирует весь код и определяет, какие классы/методы достижимы  
Удаляет недостижимый код (dead code elimination)  
Компилирует в машинный код для целевой платформы (Linux, macOS, Windows)  
Включает только нужные части JDK (не весь JDK, а только используемые классы)  
Генерирует image heap — предсозданную кучу с инициализированными объектами  