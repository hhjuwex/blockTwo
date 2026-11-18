package org.sky.service;

import org.sky.service.billing.CampingStore;
import org.sky.service.model.CorporateTent;
import org.sky.service.model.SleepingBag;
import org.sky.service.model.Tent;
import org.sky.service.model.UserTent;
import org.sky.service.service.impl.EmailNotification;
import org.sky.service.service.impl.SmsNotification;

public class Main {

    public static void main(String[] args) {

        System.out.println("Аренда снаряжения");
        System.out.println();

        // 1. Создание спального мешка
        SleepingBag sleepingBag = new SleepingBag("Warm Bag", 800);

        // 2. Магазин с SMS-уведомлениями
        // Аренда для физического лица
        CampingStore smsStore = new CampingStore(new SmsNotification());

        Tent userTent = new UserTent(
                "Family Tent", 3000, "4-person"
        );

        double price1 = userTent.calculateFinalPrice(sleepingBag, true);
        smsStore.processPayment(userTent, "+79111234567", price1);

        System.out.println();

        // 3. Магазин с Email-уведомлениями
        // Аренда для юридического лица на 5 палаток
        CampingStore emailStore = new CampingStore(new EmailNotification());

        Tent corporateTent = new CorporateTent(
                "Big Camping Tent", 4000, "CampingTent", 5
        );

        double price2 = corporateTent.calculateFinalPrice();
        emailStore.processPayment(corporateTent, "tour@camp.ru", price2);

        System.out.println();

        // 4. Проверки некорректных данных
        System.out.println("--- Проверки ---");

        // Некорректная цена и неизвестный размер
        Tent badTent = new UserTent(
                "Broken Tent", -500, "10-person"
        );

        double price3 = badTent.calculateFinalPrice();
        smsStore.processPayment(badTent, "12345", price3);

        System.out.println();

        // Некорректное количество палаток и зимний спальник
        Tent badCorporate = new CorporateTent(
                "Small Tent", 2000, "2-person", 0
        );

        double price4 = badCorporate.calculateFinalPrice(sleepingBag, true);
        emailStore.processPayment(badCorporate, "wrong-email", price4);

        System.out.println();

        // 5. Глобальная статистика
        CampingStore.printGlobalStats();
    }
}