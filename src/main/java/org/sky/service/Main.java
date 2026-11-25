package org.sky.service;

import net.datafaker.Faker;
import org.sky.service.billing.CampingStore;
import org.sky.service.exception.DuplicateSubscriptionException;
import org.sky.service.exception.InsufficientFundsException;
import org.sky.service.exception.InvalidSubscriptionException;
import org.sky.service.exception.SubscriptionNotFoundException;
import org.sky.service.model.CorporateTent;
import org.sky.service.model.PromoCode;
import org.sky.service.model.SleepingBag;
import org.sky.service.model.Tent;
import org.sky.service.model.UserTent;
import org.sky.service.service.impl.EmailNotification;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        System.out.println("Аренда туристического снаряжения");
        System.out.println();

        CampingStore store = new CampingStore(
                new EmailNotification()
        );

        // 1. Генерация 50 клиентов через Datafaker
        generateTestUsers(store, 50);

        // 2. Аналитика через Stream API
        System.out.println("\n=== АНАЛИТИКА STREAM API ===");

        System.out.println("Всего записей в базе: "
                + store.getDatabase().size());
        System.out.println("Прогнозируемая выручка: "
                + store.calculateTotalRevenue() + " руб.");
        System.out.println("Палаток размера 4-person: "
                + store.countBySize("4-person"));

        // Поиск VIP-клиентов (чек > 10000 руб.)
        List<Tent> vipClients = store.findVipClients(10000.0);
        System.out.println("VIP-клиентов (чек > 10000 руб.): "
                + vipClients.size());

        vipClients.stream()
                .limit(5)
                .forEach(tent -> System.out.printf(
                        "  * %s [%s] — %.2f руб. (услуг: %d)%n",
                        tent.getName(),
                        tent.getSize(),
                        tent.calculateFinalPrice(),
                        tent.getAppliedBags().size()
                ));

        // Группировка по размерам палаток
        System.out.println("\nРаспределение по размерам:");
        Map<String, List<Tent>> grouped = store.groupTentsBySize();
        grouped.forEach((size, tents) -> System.out.printf(
                "  - Размер %s: %d шт.%n", size, tents.size()
        ));

        // 3. Демонстрация оплаты и обработки исключений
        System.out.println("\n=== ОПЛАТА И ОБРАБОТКА ИСКЛЮЧЕНИЙ ===");

        // 3.1: успешная оплата первого сгенерированного клиента
        try {
            String existingEmail = store.getDatabase().keySet()
                    .iterator().next();
            store.processPaymentByEmail(existingEmail);

        } catch (SubscriptionNotFoundException |
                 InsufficientFundsException e) {

            System.out.println("[ERROR] " + e.getMessage());
        }

        // 3.2: несуществующий Email
        try {
            store.processPaymentByEmail("fake@mail.com");

        } catch (SubscriptionNotFoundException |
                 InsufficientFundsException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());
        }

        // 3.3: некорректная цена палатки
        try {
            new UserTent("Broken Tent", -500, "2-person");

        } catch (InvalidSubscriptionException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());
        }

        // 3.4: дубликат регистрации
        try {
            String existingEmail = store.getDatabase().keySet()
                    .iterator().next();
            store.registerTent(
                    existingEmail,
                    new UserTent("Another Tent", 2000)
            );

        } catch (DuplicateSubscriptionException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());
        }

        // 3.5: недостаточно средств (регистрируем клиента без баланса)
        try {
            store.registerTent(
                    "poor@mail.ru",
                    new UserTent("Small Tent", 2000, "2-person")
            );
            store.processPaymentByEmail("poor@mail.ru");

        } catch (InsufficientFundsException |
                 SubscriptionNotFoundException |
                 DuplicateSubscriptionException e) {

            System.out.println("[CAUGHT EXCEPTION] " + e.getMessage());

        } finally {
            System.out.println("Проверка оплаты завершена.");
        }

        // Итоговая статистика оплат
        CampingStore.printGlobalStats();

        System.out.println("\nEnd");
    }

    // Метод-генератор тестовых клиентов с помощью библиотеки Datafaker
    private static void generateTestUsers(CampingStore store, int count) {

        Faker faker = new Faker(new Locale("ru"));
        // Отдельный Faker с латиницей для валидных Email-адресов
        Faker emailFaker = new Faker();
        String[] sizes = {"2-person", "4-person", "CampingTent"};
        String[] bagNames = {
                "Warm Bag", "Summer Bag", "Ultra Light Bag", "Family Bag"
        };

        for (int i = 0; i < count; i++) {

            // Индекс в начале гарантирует уникальность ключей в HashMap
            String email = i + "_" + emailFaker.internet().emailAddress();
            String name = faker.commerce().productName();
            String size = sizes[faker.random().nextInt(sizes.length)];
            double basePrice = faker.number().randomDouble(2, 1500, 6000);

            Tent tent;

            // Каждый 5-й клиент — корпоративный (демонстрация @SuperBuilder)
            if (i % 5 == 0) {
                tent = CorporateTent.builder()
                        .name(name)
                        .basePrice(basePrice)
                        .size(size)
                        .tentsCount(faker.number().numberBetween(2, 10))
                        .build();

            } else {
                tent = UserTent.builder()
                        .name(name)
                        .basePrice(basePrice)
                        .size(size)
                        .build();

                // От 1 до 3 случайных спальных мешков
                int bagsCount = faker.number().numberBetween(1, 4);
                for (int j = 0; j < bagsCount; j++) {
                    tent.addBag(new SleepingBag(
                            bagNames[faker.random().nextInt(bagNames.length)],
                            faker.number().numberBetween(300, 1200)
                    ));
                }

                // С вероятностью 50% применяем промокод
                if (faker.bool().bool()) {
                    tent.setAppliedPromo(PromoCode.builder()
                            .code("CAMP" + faker.number().digits(2))
                            .discountPercent(
                                    faker.number().numberBetween(10, 30))
                            .build());
                }
            }

            try {
                store.registerTent(email, tent);
                // Пополняем баланс, чтобы клиент мог оплатить аренду
                store.addFunds(email, faker.number()
                        .numberBetween(5000, 100000));
            } catch (DuplicateSubscriptionException |
                     SubscriptionNotFoundException e) {

                System.out.println("[ERROR] " + e.getMessage());
            }
        }

        System.out.println("Сгенерировано клиентов: "
                + store.getDatabase().size());
    }
}
