package com.example.Notification_hub.service;


import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSender {
    private final NotificationRepository repository;
    public void send(Notification notification){
        try{
            log.info("Поток [{}] взял в работу уведомление ID {} для {}", Thread.currentThread().getName(), notification.getId(), notification.getRecipient());
            Thread.sleep(2000);

            notification.setStatus("SENT");
            repository.save(notification);
            log.info("Уведомление с ID {} успешно отправлено!", notification.getId());
        } catch (InterruptedException e) {
           log.error("Ошибка при отпраке: ", e);
           Thread.currentThread().interrupt();
        }


    }
}
