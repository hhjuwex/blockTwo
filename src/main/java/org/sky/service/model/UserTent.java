package org.sky.service.model;

import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(callSuper = true)
@SuperBuilder
public class UserTent extends Tent {

    public UserTent(String name, double basePrice, String size) {
        super(name, basePrice, size);
    }

    public UserTent(String name, double basePrice) {
        super(name, basePrice);
    }

    @Override
    public double calculateFinalPrice() {

        double totalPrice = getPriceWithSize() + calculateBagsTotal();

        return Math.round(applyPromoDiscount(totalPrice) * 100.0) / 100.0;
    }
}
