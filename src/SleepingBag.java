public class SleepingBag {
    private final String name;
    private double basePrice;

    public SleepingBag(String name, double basePrice) {
        if (basePrice < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }

        this.name = name;
        this.basePrice = basePrice;
    }

    //сетееер
    public void setBasePrice(double basePrice) {
        if (basePrice < 0) {
            throw new IllegalArgumentException(
                    "Цена не может быть отрицательной"
            );
        }

        this.basePrice = basePrice;
    }


    //гетрыы
    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }

}
