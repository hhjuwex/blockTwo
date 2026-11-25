package org.sky.service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.sky.service.service.NotificationService;

@Slf4j
public class EmailNotification implements NotificationService {

    @Override
    public void sendNotification(String message, String recipient) {
        if (recipient == null || !recipient.contains("@") || !recipient.contains(".")) {
            log.warn("Некорректный email: {}. Письмо не отправлено", recipient);
            return;
        }

        log.info("[EMAIL] На адрес {} отправлено письмо: {}", recipient, message);
    }
}
