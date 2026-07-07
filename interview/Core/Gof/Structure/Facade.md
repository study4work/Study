## Facade (Фасад)

Суть: Предоставляет простой интерфейс к сложной системе классов.

```java
// Сложная подсистема
public class CPU {
    public void freeze() { System.out.println("CPU: заморозка"); }
    public void jump(long position) { System.out.println("CPU: переход на " + position); }
    public void execute() { System.out.println("CPU: выполнение"); }
}

public class Memory {
    public void load(long position, byte[] data) {
        System.out.println("Memory: загрузка данных на " + position);
    }
}

public class HardDrive {
    public byte[] read(long lba, int size) {
        System.out.println("HardDrive: чтение с сектора " + lba);
        return new byte[size];
    }
}

// Фасад
public class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    
    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }
    
    public void start() {
        System.out.println("ComputerFacade: запуск компьютера...");
        cpu.freeze();
        memory.load(0, hardDrive.read(0, 1024));
        cpu.jump(0);
        cpu.execute();
        System.out.println("ComputerFacade: компьютер запущен!");
    }
    
    public void shutdown() {
        System.out.println("ComputerFacade: выключение компьютера...");
        // логика выключения
    }
}

// Использование:
ComputerFacade computer = new ComputerFacade();
computer.start();
// ComputerFacade: запуск компьютера...
// CPU: заморозка
// HardDrive: чтение с сектора 0
// Memory: загрузка данных на 0
// CPU: переход на 0
// CPU: выполнение
// ComputerFacade: компьютер запущен!
```