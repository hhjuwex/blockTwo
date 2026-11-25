package org.sky.service.billing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sky.service.exception.DuplicateSubscriptionException;
import org.sky.service.exception.InvalidSubscriptionException;
import org.sky.service.exception.SubscriptionNotFoundException;
import org.sky.service.model.CorporateTent;
import org.sky.service.model.PromoCode;
import org.sky.service.model.SleepingBag;
import org.sky.service.model.Tent;
import org.sky.service.model.UserTent;
import org.sky.service.service.impl.EmailNotification;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampingStoreTest {

    private CampingStore store;

    // Выполняется перед каждым тестом, создавая чистую базу
    @BeforeEach
    void setUp() {
        store = new CampingStore(new EmailNotification());
    }

    @Test
    @DisplayName("1. Расчет цены: размер 4-person + 2 спальника + промокод 20%")
    void shouldCalculatePriceWithBagsAndPromoCode() {
        // Arrange
        // База: 3000 руб., размер 4-person (коэф. 1.6) -> 4800 руб.
        UserTent tent = new UserTent("Family Tent", 3000, "4-person");

        // Два спальника: 500 + 700 = 1200 руб. (итого до скидки 6000 руб.)
        tent.addBag(new SleepingBag("Summer Bag", 500));
        tent.addBag(new SleepingBag("Warm Bag", 700));

        // Промокод на скидку 20% (6000 * 0.80 = 4800 руб.)
        tent.setAppliedPromo(new PromoCode("CAMP20", 20));

        // Act
        double actualPrice = tent.calculateFinalPrice();

        // Assert
        assertEquals(4800.0, actualPrice, 0.01,
                "Итоговая цена должна учитывать размер, услуги и скидку 20%");
    }

    @Test
    @DisplayName("2. Исключение: отрицательная цена при создании палатки")
    void shouldThrowExceptionWhenPriceIsNegative() {
        // Arrange
        double invalidPrice = -150.0;

        // Act & Assert
        assertThrows(InvalidSubscriptionException.class, () ->
                        new UserTent("Broken Tent", invalidPrice, "2-person"),
                "Отрицательная цена должна вызывать InvalidSubscriptionException");
    }

    @Test
    @DisplayName("3. Исключение: попытка оплаты несуществующего Email")
    void shouldThrowExceptionWhenEmailNotFound() {
        // Arrange
        String nonExistentEmail = "ghost@camping.ru";

        // Act & Assert
        assertThrows(SubscriptionNotFoundException.class, () ->
                        store.processPaymentByEmail(nonExistentEmail),
                "Оплата по отсутствующему email должна выбрасывать "
                        + "SubscriptionNotFoundException");
    }

    @Test
    @DisplayName("4. Stream API: фильтрация VIP-клиентов выше порога")
    void shouldFindOnlyVipClientsAboveThreshold() throws Exception {
        // Arrange
        // Клиент 1: 2000 руб. (2-person) -> НЕ VIP
        Tent regularTent = new UserTent("Small Tent", 2000, "2-person");
        // Клиент 2: 4000 * 1.6 = 6400 руб. -> VIP
        Tent vipTent = new UserTent("Family Tent", 4000, "4-person");
        // Клиент 3: 3000 * 5 палаток = 15000 руб. -> VIP
        Tent corpTent = new CorporateTent(
                "Big Camping", 3000, "2-person", 5);

        store.registerTent("user1@test.ru", regularTent);
        store.registerTent("user2@test.ru", vipTent);
        store.registerTent("corp@test.ru", corpTent);

        // Act
        List<Tent> vipClients = store.findVipClients(5000.0);

        // Assert
        assertEquals(2, vipClients.size(),
                "В списке должно быть ровно 2 VIP-клиента");
        assertFalse(vipClients.contains(regularTent),
                "Палатка с чеком 2000 руб. не должна попасть в VIP");
        assertTrue(vipClients.contains(vipTent),
                "VIP-клиент должен быть в списке");
    }

    @Test
    @DisplayName("5. Stream API: подсчет общей прогнозируемой выручки")
    void shouldCalculateTotalRevenueCorrectly() throws Exception {
        // Arrange
        // Палатка 1: 2000 руб. (2-person) -> 2000.0
        Tent tent1 = new UserTent("Tent One", 2000, "2-person");
        // Палатка 2: 1000 руб. + спальник 500 руб. -> 1500.0
        Tent tent2 = new UserTent("Tent Two", 1000, "2-person");
        tent2.addBag(new SleepingBag("Summer Bag", 500));

        store.registerTent("a@test.ru", tent1);
        store.registerTent("b@test.ru", tent2);

        // Act
        double totalRevenue = store.calculateTotalRevenue();

        // Assert (ожидаем 2000.0 + 1500.0 = 3500.0)
        assertEquals(3500.0, totalRevenue, 0.01,
                "Выручка должна быть равна сумме чеков всех палаток");
    }

    @Test
    @DisplayName("6. Stream API: группировка палаток по размерам")
    void shouldGroupTentsBySize() throws Exception {
        // Arrange
        store.registerTent("a@test.ru",
                new UserTent("Tent A", 2000, "2-person"));
        store.registerTent("b@test.ru",
                new UserTent("Tent B", 3000, "2-person"));
        store.registerTent("c@test.ru",
                new UserTent("Tent C", 4000, "4-person"));

        // Act
        Map<String, List<Tent>> grouped = store.groupTentsBySize();

        // Assert
        assertEquals(2, grouped.get("2-person").size(),
                "Должно быть 2 палатки размера 2-person");
        assertEquals(1, grouped.get("4-person").size(),
                "Должна быть 1 палатка размера 4-person");
        assertEquals(2, store.countBySize("2-person"),
                "countBySize должен вернуть 2 для размера 2-person");
    }

    @Test
    @DisplayName("7. Исключение: дубликат регистрации Email")
    void shouldThrowExceptionOnDuplicateEmail() throws Exception {
        // Arrange
        store.registerTent("dup@test.ru",
                new UserTent("Tent A", 2000, "2-person"));

        // Act & Assert
        assertThrows(DuplicateSubscriptionException.class, () ->
                        store.registerTent("dup@test.ru",
                                new UserTent("Tent B", 3000, "2-person")),
                "Повторная регистрация email должна выбрасывать "
                        + "DuplicateSubscriptionException");
    }
}
