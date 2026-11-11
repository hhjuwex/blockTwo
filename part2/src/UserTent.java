
public class UserTent extends Tent {

    public UserTent(String name, double basePrice, String size) {
        super(name, basePrice, size);
    }

    public UserTent(String name, double basePrice) {
        super(name, basePrice);
    }

    @Override
    public double calculateFinalPrice() {
        return getPriceWithSize();
    }
}
