package org.sky.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class SleepingBag {

    private final String name;

    @Setter
    private double basePrice;

    public SleepingBag(String name, double basePrice) {
        if (basePrice < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }

        this.name = name;
        this.basePrice = basePrice;
    }
}
