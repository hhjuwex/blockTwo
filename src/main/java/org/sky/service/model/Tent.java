package org.sky.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.sky.service.exception.InvalidSubscriptionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@SuperBuilder
public abstract class Tent {

    private static final double TWO_PERSON_COEFFICIENT = 1.0;
    private static final double FOUR_PERSON_COEFFICIENT = 1.6;
    private static final double CAMPING_TENT_COEFFICIENT = 2.3;
    private static final double DEFAULT_COEFFICIENT = 1.0;
    private static final double WARM_SLEEPING_BAG_COEFFICIENT = 1.5;

    private final String name;
    private final double basePrice;
    private final String size;

    // Список дополнительных услуг
    @Builder.Default
    private final List<SleepingBag> appliedBags = new ArrayList<>();

    // Промокод со скидкой (может отсутствовать)
    @Setter
    private PromoCode appliedPromo;

    public Tent(String name, double basePrice, String size) {

        if (basePrice <= 0) {
            throw new InvalidSubscriptionException(
                    "Некорректная цена палатки"
            );
        }

        if (name == null || name.isBlank()) {
            throw new InvalidSubscriptionException(
                    "Не указано название палатки"
            );
        }

        this.name = name;
        this.basePrice = basePrice;
        this.size = size;
        this.appliedBags = new ArrayList<>();
    }

    public Tent(String name, double basePrice) {
        this(name, basePrice, "2-person");
    }

    // Основной метод расчета стоимости
    public abstract double calculateFinalPrice();

    protected double getPriceWithSize() {
        return basePrice * getSizeCoefficient();
    }

    protected double getSizeCoefficient() {
        return switch (size) {
            case "2-person" -> TWO_PERSON_COEFFICIENT;
            case "4-person" -> FOUR_PERSON_COEFFICIENT;
            case "CampingTent" -> CAMPING_TENT_COEFFICIENT;

            default -> {
                System.out.println(
                        "[ADMIN] Неизвестный размер палатки: \""
                                + size + "\". Применен коэффициент "
                                + DEFAULT_COEFFICIENT
                );

                yield DEFAULT_COEFFICIENT;
            }
        };
    }

    // Сумма всех дополнительных услуг (Stream API)
    protected double calculateBagsTotal() {
        return appliedBags.stream()
                .mapToDouble(SleepingBag::getBasePrice)
                .sum();
    }

    // Применение скидки по промокоду (если он задан)
    protected double applyPromoDiscount(double totalPrice) {
        if (appliedPromo != null) {
            double discount = appliedPromo.getDiscountPercent() / 100.0;
            return totalPrice * (1.0 - discount);
        }

        return totalPrice;
    }

    // Добавление обычного спальника
    public void addBag(SleepingBag bag) {
        addBag(bag, false);
    }

    // Добавление спальника с учетом зимнего коэффициента
    public void addBag(SleepingBag bag, boolean warm) {

        if (bag == null) {
            throw new IllegalArgumentException(
                    "Нельзя добавить пустой спальный мешок!"
            );
        }

        double bagPrice = bag.getBasePrice();

        if (warm) {
            bagPrice *= WARM_SLEEPING_BAG_COEFFICIENT;
        }

        // Сохраняем отдельный объект с рассчитанной стоимостью
        appliedBags.add(new SleepingBag(bag.getName(), bagPrice));
    }

    public List<SleepingBag> getAppliedBags() {
        return Collections.unmodifiableList(appliedBags);
    }
}
