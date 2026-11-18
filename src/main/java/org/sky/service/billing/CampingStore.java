package org.sky.service.billing;

import org.sky.service.model.Tent;
import org.sky.service.service.NotificationService;

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
                "Оплата прошла. Палатка: %s | Размер: %s | К оплате: %.2f%n",
                tent.getName(),
                tent.getSize(),
                finalPrice
        );

        if (tent.getAppliedOption() != null) {
            System.out.printf(
                    "Добавлен спальный мешок: %s | Цена: %.2f%n",
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
        System.out.println("              ГЛОБАЛЬНАЯ СТАТИСТИКА");
        System.out.println("==================================================");
        System.out.println("Всего заказов: " + totalOrdersCount);
        System.out.println(
                "Выручка: " + Math.round(totalRevenue * 100.0) / 100.0
        );
        System.out.println("==================================================");
    }
}