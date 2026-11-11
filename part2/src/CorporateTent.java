
public class CorporateTent extends Tent {

    private final int tentsCount;

    public CorporateTent(String name, double basePrice, String size, int tentsCount) {
        super(name, basePrice, size);

        if (tentsCount < 1) {
            System.out.println("[WARNING] Количество палаток " + tentsCount
                    + " некорректно, установлено: 1");
            tentsCount = 1;
        }
        this.tentsCount = tentsCount;
    }

    @Override
    public double calculateFinalPrice() {
        return getPriceWithSize() * tentsCount;
    }

    @Override
    public double calculateFinalPrice(SleepingBag bag, boolean isWarm) {
        if (isWarm) {
            System.out.println("[WARNING] Для корпоративных клиентов зимний спальник"
                    + " недоступен, посчитана стандартная цена");
        }

        return calculateFinalPrice(bag);
    }

    //гетрыы
    public int getTentsCount() {
        return tentsCount;
    }
}
