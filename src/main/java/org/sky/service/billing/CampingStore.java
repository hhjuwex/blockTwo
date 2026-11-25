package org.sky.service.billing;

import org.sky.service.exception.DuplicateSubscriptionException;
import org.sky.service.exception.InsufficientFundsException;
import org.sky.service.exception.InvalidEmailException;
import org.sky.service.exception.SubscriptionNotFoundException;
import org.sky.service.model.SleepingBag;
import org.sky.service.model.Tent;
import org.sky.service.service.NotificationService;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CampingStore {

    private static int totalOrdersCount = 0;
    private static double totalRevenue = 0;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Реестр клиентов: Email -> арендуемая палатка
    private final Map<String, Tent> database = new HashMap<>();

    // Баланс каждого зарегистрированного клиента
    private final Map<String, Double> balances = new HashMap<>();

    private final NotificationService notificationService;

    public CampingStore(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Регистрация клиента
    public void registerTent(String email, Tent tent)
            throws DuplicateSubscriptionException {

        String normalizedEmail = normalizeEmail(email);

        if (tent == null) {
            throw new IllegalArgumentException(
                    "Нельзя зарегистрировать пустую палатку."
            );
        }

        if (database.containsKey(normalizedEmail)) {
            throw new DuplicateSubscriptionException(
                    "Клиент с Email " + normalizedEmail
                            + " уже зарегистрирован."
            );
        }

        database.put(normalizedEmail, tent);
        balances.put(normalizedEmail, 0.0);

        System.out.println(
                "Клиент зарегистрирован: " + normalizedEmail
        );
    }

    // Пополнение баланса клиента
    public void addFunds(String email, double amount)
            throws SubscriptionNotFoundException {

        String normalizedEmail = normalizeEmail(email);

        if (!database.containsKey(normalizedEmail)) {
            throw new SubscriptionNotFoundException(
                    "Клиент " + normalizedEmail
                            + " не зарегистрирован."
            );
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                    "Сумма пополнения должна быть больше нуля."
            );
        }

        double currentBalance = balances.get(normalizedEmail);
        balances.put(normalizedEmail, currentBalance + amount);
    }

    // Проведение оплаты по Email
    public void processPaymentByEmail(String email)
            throws SubscriptionNotFoundException,
            InsufficientFundsException {

        String normalizedEmail = normalizeEmail(email);

        Tent tent = database.get(normalizedEmail);

        if (tent == null) {
            throw new SubscriptionNotFoundException(
                    "Пользователь с email " + normalizedEmail
                            + " не зарегистрирован в CampingStore."
            );
        }

        // Рассчитываем стоимость всей аренды
        double finalPrice = tent.calculateFinalPrice();

        double balance = balances.get(normalizedEmail);

        // Проверяем достаточность средств
        if (balance < finalPrice) {
            throw new InsufficientFundsException(
                    "Недостаточно средств! Баланс: "
                            + balance + ", требуется: " + finalPrice
            );
        }

        // Списываем средства
        balances.put(normalizedEmail, balance - finalPrice);

        // Обновляем глобальную статистику
        totalRevenue += finalPrice;
        totalOrdersCount++;

        System.out.printf(
                "Оплата прошла. Палатка: %s | Размер: %s | К оплате: %.2f%n",
                tent.getName(),
                tent.getSize(),
                finalPrice
        );

        // Выводим все дополнительные услуги
        for (SleepingBag bag : tent.getAppliedBags()) {
            System.out.printf(
                    "Добавлен спальный мешок: %s | Цена: %.2f%n",
                    bag.getName(),
                    bag.getBasePrice()
            );
        }

        String receipt = String.format(
                "Чек: аренда \"%s\" на сумму %.2f руб.",
                tent.getName(),
                finalPrice
        );

        notificationService.sendNotification(receipt, normalizedEmail);
    }

    // Проверка и нормализация Email
    private String normalizeEmail(String email) {

        if (email == null
                || !EMAIL_PATTERN.matcher(email.trim()).matches()) {

            throw new InvalidEmailException(
                    "Некорректный Email: " + email
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    public double getBalance(String email) {
        String normalizedEmail = normalizeEmail(email);
        return balances.getOrDefault(normalizedEmail, 0.0);
    }

    public Map<String, Tent> getDatabase() {
        return database;
    }

    // 1. Поиск VIP-клиентов с чеком выше заданной суммы
    public List<Tent> findVipClients(double minAmount) {
        return database.values().stream()
                .filter(tent -> tent.calculateFinalPrice() > minAmount)
                .toList();
    }

    // 2. Прогнозируемая выручка по всей базе
    public double calculateTotalRevenue() {
        double total = database.values().stream()
                .mapToDouble(Tent::calculateFinalPrice)
                .sum();
        return Math.round(total * 100.0) / 100.0;
    }

    // 3. Подсчет количества палаток определенного размера
    public long countBySize(String size) {
        return database.values().stream()
                .filter(tent -> tent.getSize().equalsIgnoreCase(size))
                .count();
    }

    // 4. Группировка палаток по размерам (Collectors.groupingBy)
    public Map<String, List<Tent>> groupTentsBySize() {
        return database.values().stream()
                .collect(Collectors.groupingBy(Tent::getSize));
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