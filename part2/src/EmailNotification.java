public class EmailNotification implements NotificationService {

    @Override
    public void sendNotification(String message, String recipient) {
        //проверка адреса
        if (recipient == null || !recipient.contains("@") || !recipient.contains(".")) {
            System.out.println("[WARNING] Некорректный email: " + recipient
                    + ". Письмо не отправлено");
            return;
        }

        System.out.println("[EMAIL] На адрес " + recipient
                + " отправлено письмо: " + message);
    }
}
