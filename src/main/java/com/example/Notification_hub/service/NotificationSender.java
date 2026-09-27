package com.example.Notification_hub.service;


import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.entity.NotificationChannel;
import com.example.Notification_hub.entity.NotificationStatus;
import com.example.Notification_hub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationSender {
    private final NotificationRepository repository;
    private final Map<NotificationChannel, ChannelSender> senders;

    public NotificationSender(NotificationRepository repository, List<ChannelSender> senderList) {
        this.repository = repository;
        this.senders = senderList.stream().
                collect(Collectors.toMap(ChannelSender::getChannel, Function.identity()));
    }



    @Async("notificationExecutor")
    public void send(Notification notification) {
       try {
           ChannelSender sender = senders.get(notification.getChannel());
           if (sender == null){
               throw new IllegalArgumentException("Канал не поддерживатеся " + notification.getChannel());
           }
           sender.send(notification);
           notification.setStatus(NotificationStatus.SENT);
       }catch (Exception e){
           int retries = notification.getRetryCount() + 1;
           notification.setRetryCount(retries);
           if (retries >= 3){
               log.error("Исперпан лимит попыток для ID {}. Статус: FAILED. Ошибка: {}", notification.getId(), e.getMessage());
               notification.setStatus(NotificationStatus.FAILED);
           }else{
               log.warn("Ошибка отправки для ID {} (попытка {}/3). Вернули в PENDING", notification.getId(), notification.getRetryCount());
               notification.setStatus(NotificationStatus.PENDING);
           }
           repository.save(notification);
       }
    }
}
