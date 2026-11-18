package org.sky.service;

import org.sky.service.billing.CampingStore;
import org.sky.service.exception.DuplicateSubscriptionException;
import org.sky.service.exception.InsufficientFundsException;
import org.sky.service.exception.InvalidEmailException;
import org.sky.service.exception.InvalidSubscriptionException;
import org.sky.service.exception.SubscriptionNotFoundException;
import org.sky.service.model.CorporateTent;
import org.sky.service.model.SleepingBag;
import org.sky.service.model.Tent;
import org.sky.service.model.UserTent;
import org.sky.service.service.impl.EmailNotification;

public class Main {

    public static void main(String[] args) {

        System.out.println("Аренда туристического снаряжения");
        System.out.println();

        // Создание доп услуг

        SleepingBag warmBag = new SleepingBag("Warm Bag", 800);
        SleepingBag summerBag = new SleepingBag("Summer Bag", 500);

        // Создание магазина

        CampingStore store = new CampingStore(
                new EmailNotification()
        );

        // Создание палаток

        // Физическое лицо
        Tent userTent = new UserTent(
                "Family Tent", 3000, "4-person"
        );

        userTent.addBag(warmBag, true);
        userTent.addBag(summerBag);

        // Корпоративный клиент
        Tent corporateTent = new CorporateTent(
                "Big Camping Tent", 4000, "CampingTent", 5
        );

        corporateTent.addBag(warmBag);
        corporateTent.addBag(summerBag, true);

        // Клиент без средств на балансе
        Tent poorTent = new UserTent(
                "Small Tent", 2000, "2-person"
        );

        poorTent.addBag(warmBag);
        poorTent.addBag(summerBag);

        // Рег. клиентов

        try {
            store.registerTent("ivan@mail.ru", userTent);
            store.addFunds("ivan@mail.ru", 10000);

            store.registerTent("tour@camp.ru", corporateTent);
            store.addFunds("tour@camp.ru", 50000);

            // Регистрируем клиента без пополнения баланса
            store.registerTent("poor@mail.ru", poorTent);

        } catch (DuplicateSubscriptionException |
                 SubscriptionNotFoundException e) {

            System.out.println("[ERROR] " + e.getMessage());
            return;
        }

        // 1: успешная оплата

        System.out.println("\n1: Успешная оплата");

        try {
            store.processPaymentByEmail("ivan@mail.ru");
            store.processPaymentByEmail("tour@camp.ru");

        } catch (SubscriptionNotFoundException |
                 InsufficientFundsException e) {

            System.out.println("[ERROR] " + e.getMessage());
        }

        // 2: несушествующее мыло

        System.out.println("\n2: Несуществующий Email");

        try {
            store.processPaymentByEmail("fake@mail.com");

        } catch (SubscriptionNotFoundException |
                 InsufficientFundsException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());
        }


        // неправильная стоимость

        System.out.println("\n3: Некорректная цена");

        try {
            Tent badTent = new UserTent(
                    "Broken Tent", -500, "2-person"
            );

        } catch (InvalidSubscriptionException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());
        }


        // дубликат регистрации

        System.out.println("\n4: Дубликат Email");

        try {
            store.registerTent(
                    "ivan@mail.ru",
                    new UserTent("Another Tent", 2000)
            );

        } catch (DuplicateSubscriptionException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());
        }

        // неправильное мыло

        System.out.println("\n5: Неправильный Email");

        try {
            store.registerTent(
                    "wrong-email",
                    new UserTent("Invalid Email Tent", 1500)
            );

        } catch (InvalidEmailException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());
        } catch (DuplicateSubscriptionException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());
        }

        // нет денег

        System.out.println("\n6: Недостаточно средств");

        try {
            store.processPaymentByEmail("poor@mail.ru");

        } catch (InsufficientFundsException |
                 SubscriptionNotFoundException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());

        } finally {
            System.out.println("Проверка оплаты завершена.");
        }

        // null

        System.out.println("\n7: Добавление null");

        try {
            userTent.addBag(null);

        } catch (IllegalArgumentException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());
        }

        // Статистика

        System.out.println("\n--- Итоговая статистика ---");

        CampingStore.printGlobalStats();

        System.out.println("\nEnd");
    }
}