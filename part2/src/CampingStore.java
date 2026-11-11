public class CampingStore {
    private static int totalOrdersCount = 0;
    private static double totalRevenue = 0;
    private final NotificationService notificationService;

    public CampingStore(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void processPayment(Tent tent, String contact, double finalPrice) {
        totalRevenue += finalPrice;
        totalOrdersCount++;

        System.out.printf(
                "Оплата прошла. Палатка: %s | Размер: %s | К оплате: %.2f %n",
                tent.getName(),
                tent.getSize(),
                finalPrice
        );

        if (tent.getAppliedOption() != null) {
            System.out.printf(
                    "Добавлен спальный мешок: %s | Цена: %.2f %n",
                    tent.getAppliedOption().getName(),
                    tent.getAppliedOption().getBasePrice()
            );
        }

        String receipt = String.format(
                "Чек: аренда \"%s\" на сумму %.2f руб.",
                tent.getName(),
                finalPrice
        );
        notificationService.sendNotification(receipt, contact);
    }


    public static int getTotalOrdersCount() {
        return totalOrdersCount;
    }

    public static double getTotalRevenue() {
        return totalRevenue;
    }

    public static void printGlobalStats() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("              ГЛОБАЛЬНАЯ СТАТИСТИКА    ");
        System.out.println("==================================================");
        System.out.println(
                "Всего заказов: " + totalOrdersCount
        );
        System.out.println(
                "Выручка: "
                        + Math.round(totalRevenue * 100.0) / 100.0
        );
        System.out.println("==================================================");
    }

    public static void main(String[] args) {

        System.out.println("Аренда снаряжения");
        System.out.println();

        // 1 спальный мешок
        SleepingBag sleepingBag = new SleepingBag("Warm Bag", 800);

        // 2 магазин с смс
        // + аренда для физ лица
        CampingStore smsStore = new CampingStore(new SmsNotification());

        Tent userTent = new UserTent("Family Tent", 3000, "4-person");

        double price1 = userTent.calculateFinalPrice(sleepingBag, true);
        smsStore.processPayment(userTent, "+79111234567", price1);

        System.out.println();

        // 3 магазин с email уведомлениями
        // аренда для юр лица на 5 палаток
        CampingStore emailStore = new CampingStore(new EmailNotification());

        Tent corporateTent = new CorporateTent("Big Camping Tent", 4000, "CampingTent", 5);

        double price2 = corporateTent.calculateFinalPrice();
        emailStore.processPayment(corporateTent, "tour@camp.ru", price2);

        System.out.println();

        // 4 проверки некорректных данных
        System.out.println("--- Проверки ---");

        // цена <= 0 и неизвестный размер
        Tent badTent = new UserTent("Broken Tent", -500, "10-person");

        double price3 = badTent.calculateFinalPrice();
        smsStore.processPayment(badTent, "12345", price3);

        System.out.println();

        // количество палаток < 1 и зимний мешок для юр лица
        Tent badCorporate = new CorporateTent("Small Tent", 2000, "2-person", 0);

        double price4 = badCorporate.calculateFinalPrice(sleepingBag, true);
        emailStore.processPayment(badCorporate, "wrong-email", price4);

        System.out.println();

        // 5 итогои
        printGlobalStats();
    }
}
