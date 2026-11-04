//аренда для физ. лица
public class UserTent extends Tent {

    public UserTent(String name, double basePrice, String size) {
        super(name, basePrice, size);
    }

    public UserTent(String name, double basePrice) {
        super(name, basePrice);
    }

    //как в 1 части: базовая цена * коэффициент размера
    @Override
    public double calculateFinalPrice() {
        return getPriceWithSize();
    }
}
