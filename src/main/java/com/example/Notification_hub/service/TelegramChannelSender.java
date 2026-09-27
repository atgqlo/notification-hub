package com.example.Notification_hub.service;

import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.entity.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TelegramChannelSender implements ChannelSender {

    @Override
    public void send(Notification notification) {
        log.info("Отпрвка TELEGRAM сообщения пользователю {}", notification.getRecipient());
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.Telegram;
    }
}
