## Observer

Суть: Определяет зависимость "один-ко-многим" между объектами.
```java
// Наблюдатель
public interface Observer {
    void update(String message);
}

// Субъект
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

// Конкретный субъект
public class NewsAgency implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private String news;
    
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(news);
        }
    }
    
    public void setNews(String news) {
        this.news = news;
        notifyObservers();
    }
}

// Конкретные наблюдатели
public class NewsChannel implements Observer {
    private String name;
    
    public NewsChannel(String name) {
        this.name = name;
    }
    
    @Override
    public void update(String news) {
        System.out.println(name + " получила новость: " + news);
    }
}

// Использование:
NewsAgency agency = new NewsAgency();

Observer channel1 = new NewsChannel("CNN");
Observer channel2 = new NewsChannel("BBC");
Observer channel3 = new NewsChannel("RT");

agency.attach(channel1);
agency.attach(channel2);
agency.attach(channel3);

agency.setNews("Breaking: Java 21 released!");
// CNN получила новость: Breaking: Java 21 released!
// BBC получила новость: Breaking: Java 21 released!
// RT получила новость: Breaking: Java 21 released!

agency.detach(channel2);

agency.setNews("New Spring Boot version");
// CNN получила новость: New Spring Boot version
// RT получила новость: New Spring Boot version
// (BBC больше не получает)
```