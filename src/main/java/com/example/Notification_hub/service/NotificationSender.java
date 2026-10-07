package com.example.Notification_hub.service;


import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.entity.NotificationChannel;
import com.example.Notification_hub.entity.NotificationStatus;
import com.example.Notification_hub.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(Long notificationId) {
        Notification notification = repository.findById(notificationId).
                orElseThrow(() -> new IllegalArgumentException("Уведомление не найдено"));
       try{
           ChannelSender sender = senders.get(notification.getChannel());
           if(sender == null){
               throw new IllegalArgumentException("Канал не поддерживается " + notification.getChannel());
           }
           sender.send(notification);
           notification.setStatus(NotificationStatus.SENT);
       }catch (Exception e){
           int retries = notification.getRetryCount() + 1;
           notification.setRetryCount(retries);
           if(retries >= 3){
               log.error("Исчепан лимит попыток для ID {}. error: {}", notification.getId(), e.getMessage());
               notification.setStatus(NotificationStatus.FAILED);
           }else{
               log.warn("Ошибка отправки для id {} (попыток {}/3). Вернули в PENDING", notification.getId(), notification.getRetryCount());
               notification.setStatus(NotificationStatus.PENDING);
           }
       }
       repository.save(notification);
    }
}
