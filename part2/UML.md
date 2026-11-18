# UML-диаграмма классов (часть 2)

```mermaid
classDiagram
class main.java.org.sky.service.service.NotificationService {
    <<interface>>
    +sendNotification(String message, String recipient) void
}
class main.java.org.sky.service.service.impl.EmailNotification {
    +sendNotification(String message, String recipient) void
}
class main.java.org.sky.service.service.impl.SmsNotification {
    -MIN_PHONE_LENGTH$ int
    +sendNotification(String message, String recipient) void
}
class main.java.org.sky.service.model.SleepingBag {
    -name : String
    -basePrice : double
    +main.java.org.sky.service.model.SleepingBag(String name, double basePrice)
    +setBasePrice(double basePrice) void
    +getName() String
    +getBasePrice() double
}
class main.java.org.sky.service.model.Tent {
    <<abstract>>
    -name : String
    -basePrice : double
    -size : String
    -appliedOption : main.java.org.sky.service.model.SleepingBag
    +main.java.org.sky.service.model.Tent(String name, double basePrice, String size)
    +main.java.org.sky.service.model.Tent(String name, double basePrice)
    +calculateFinalPrice()* double
    +calculateFinalPrice(main.java.org.sky.service.model.SleepingBag bag) double
    +calculateFinalPrice(main.java.org.sky.service.model.SleepingBag bag, boolean isWarm) double
    #getPriceWithSize() double
    #getSizeCoefficient() double
    +getName() String
    +getBasePrice() double
    +getSize() String
    +getAppliedOption() main.java.org.sky.service.model.SleepingBag
}
class main.java.org.sky.service.model.UserTent {
    +main.java.org.sky.service.model.UserTent(String name, double basePrice, String size)
    +main.java.org.sky.service.model.UserTent(String name, double basePrice)
    +calculateFinalPrice() double
}
class main.java.org.sky.service.model.CorporateTent {
    -tentsCount : int
    +main.java.org.sky.service.model.CorporateTent(String name, double basePrice, String size, int tentsCount)
    +calculateFinalPrice() double
    +calculateFinalPrice(main.java.org.sky.service.model.SleepingBag bag, boolean isWarm) double
    +getTentsCount() int
}
class main.java.org.sky.service.billing.CampingStore {
    -totalOrdersCount$ int
    -totalRevenue$ double
    -notificationService : main.java.org.sky.service.service.NotificationService
    +main.java.org.sky.service.billing.CampingStore(main.java.org.sky.service.service.NotificationService notificationService)
    +processPayment(main.java.org.sky.service.model.Tent tent, String contact, double finalPrice) void
    +getTotalOrdersCount()$ int
    +getTotalRevenue()$ double
    +printGlobalStats()$ void
    +main(String[] args)$ void
}

main.java.org.sky.service.service.NotificationService <|.. main.java.org.sky.service.service.impl.EmailNotification : реализует
main.java.org.sky.service.service.NotificationService <|.. main.java.org.sky.service.service.impl.SmsNotification : реализует
main.java.org.sky.service.model.Tent <|-- main.java.org.sky.service.model.UserTent : наследует
main.java.org.sky.service.model.Tent <|-- main.java.org.sky.service.model.CorporateTent : наследует
main.java.org.sky.service.model.Tent "1" o-- "0..1" main.java.org.sky.service.model.SleepingBag : appliedOption
main.java.org.sky.service.billing.CampingStore "1" --> "1" main.java.org.sky.service.service.NotificationService : использует (DI)
main.java.org.sky.service.billing.CampingStore ..> main.java.org.sky.service.model.Tent : обрабатывает
```
