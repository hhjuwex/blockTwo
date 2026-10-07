

    private static int totalOrdersCount = 0;
    private static double totalRevenue = 0;

    public static void processPayment(Tent tent, double finalPrice) {
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

        // 1. создаем спальный мешок
        SleepingBag sleepingBag = new SleepingBag("Warm Bag", 800);

        // 2. аренда 1
        // + расчет обычной цены
        Tent tent1 = new Tent("Small Tent", 2000);

        double price1 = tent1.calculateFinalPrice();
        processPayment(tent1, price1);

        System.out.println();

        // 3. аренда 2
        // + применение спального мешка
        Tent tent2 = new Tent("Family Tent", 3000, "4-person");

        double price2 =
                tent2.calculateFinalPrice(sleepingBag);
        processPayment(tent2, price2);

        System.out.println();

        // 4. аренда 3
        // + применение зимнего спального мешка
        Tent tent3 =  new Tent("Big Camping Tent", 4000, "CampingTent");

        double price3 = tent3.calculateFinalPrice(sleepingBag, true);
        processPayment(tent3, price3);

        System.out.println();

        // 5. Вывод итогов
        printGlobalStats();
    }
