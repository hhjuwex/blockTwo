public class SmsNotification implements NotificationService {
    //константыы
    private static final int MIN_PHONE_LENGTH = 11;

    @Override
    public void sendNotification(String message, String recipient) {
        //проверка номера
        if (recipient == null
                || !(recipient.startsWith("+7") || recipient.startsWith("8"))
                || recipient.length() < MIN_PHONE_LENGTH) {
            System.out.println("[WARNING] Некорректный номер: " + recipient
                    + ". SMS не отправлено");
            return;
        }

        System.out.println("[SMS] На номер " + recipient
                + " отправлено сообщение: " + message);
    }
}
