package org.sky.service.model;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class CorporateTent extends Tent {

    private final int tentsCount;

    public CorporateTent(
            String name,
            double basePrice,
            String size,
            int tentsCount
    ) {
        super(name, basePrice, size);

        if (tentsCount < 1) {
            throw new IllegalArgumentException(
                    "Некорректное количество палаток"
            );
        }

        this.tentsCount = tentsCount;
    }

    @Override
    public double calculateFinalPrice() {

        double totalPrice = getPriceWithSize() * tentsCount
                + calculateBagsTotal();

        return Math.round(applyPromoDiscount(totalPrice) * 100.0) / 100.0;
    }
}
