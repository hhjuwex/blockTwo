package org.sky.service.model;

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

        double totalPrice = getPriceWithSize() * tentsCount;

        for (SleepingBag bag : getAppliedBags()) {
            totalPrice += bag.getBasePrice();
        }

        return totalPrice;
    }

    public int getTentsCount() {
        return tentsCount;
    }
}