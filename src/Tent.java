public class Tent {
    //константыы
    private static final double TWO_PERSON_COEFFICIENT = 1.0;
    private static final double FOUR_PERSON_COEFFICIENT = 1.6;
    private static final double CAMPING_TENT_COEFFICIENT = 2.3;
    private static final double WARM_SLEEPING_BAG_COEFFICIENT = 1.5;
    //поля
    private final String name;
    private final double basePrice;
    private final String size;

    private SleepingBag appliedOption = null;
    //инициализация
    public Tent(String name, double basePrice, String size) {
        if (basePrice < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
        this.name = name;
        this.basePrice = basePrice;
        this.size = size;
    }

    public Tent(String name, double basePrice) {
        this(name, basePrice, "2-person");
    }

    public double calculateFinalPrice() {
        double coefficient = switch (size) {
            case "2-person" -> TWO_PERSON_COEFFICIENT;
            case "4-person" -> FOUR_PERSON_COEFFICIENT;
            case "CampingTent" -> CAMPING_TENT_COEFFICIENT;
            default -> throw new IllegalArgumentException(
                    "Неизвестный размер палатки: " + size
            );
        };

        return basePrice * coefficient;
    }

    public double calculateFinalPrice(SleepingBag bag) {
        appliedOption = bag;

        return calculateFinalPrice() + bag.getBasePrice();
    }

    public double calculateFinalPrice(SleepingBag bag, boolean isWarm) {
        appliedOption = bag;

        double bagPrice = isWarm
                ? bag.getBasePrice() * WARM_SLEEPING_BAG_COEFFICIENT
                : bag.getBasePrice();

        return calculateFinalPrice() + bagPrice;
    }

    //гетрыы
    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public String getSize() {
        return size;
    }

    public SleepingBag getAppliedOption() {
        return appliedOption;
    }




}





