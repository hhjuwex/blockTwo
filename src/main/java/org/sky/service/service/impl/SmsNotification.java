package org.sky.service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.sky.service.service.NotificationService;

@Slf4j
public class SmsNotification implements NotificationService {

    private static final int MIN_PHONE_LENGTH = 11;

    @Override
    public void sendNotification(String message, String recipient) {
        if (recipient == null
                || !(recipient.startsWith("+7") || recipient.startsWith("8"))
                || recipient.length() < MIN_PHONE_LENGTH) {
            log.warn("Некорректный номер: {}. SMS не отправлено", recipient);
            return;
        }

        log.info("[SMS] На номер {} отправлено сообщение: {}", recipient, message);
    }
}
