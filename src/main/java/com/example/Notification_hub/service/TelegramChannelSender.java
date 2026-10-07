package com.example.Notification_hub.service;

import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.entity.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class TelegramChannelSender implements ChannelSender {

    private final RestClient restClient;

    public TelegramChannelSender(@Value("${telegram.bot.token}") String botToken) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.telegram.org/bot" + botToken)
                .build();
    }

    @Override
    public void send(Notification notification) {
        log.info("Отправка TELEGRAM сообщения пользователю {}", notification.getRecipient());

        Map<String, String> payload = Map.of(
                "chat_id", notification.getRecipient(),
                "text", notification.getMessage()
        );
        restClient.post()
                .uri("/sendMessage")
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("Сообщение успешно отправлено в Telegram");

    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.TELEGRAM;
    }
}