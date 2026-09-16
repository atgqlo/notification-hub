package com.example.Notification_hub.service;


import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;

    public Notification createNotification(Notification notification){
        log.info("Создание нового уведомления для : {}", notification.getRecipient());

        notification.setStatus("PENDING");

        notification.setCreatedAt(java.time.LocalDateTime.now());

        return repository.save(notification);
    }


}
