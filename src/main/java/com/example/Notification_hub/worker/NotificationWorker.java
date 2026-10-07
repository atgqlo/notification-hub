package com.example.Notification_hub.worker;


import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.entity.NotificationStatus;
import com.example.Notification_hub.repository.NotificationRepository;
import com.example.Notification_hub.service.NotificationSender;
import com.example.Notification_hub.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor

public class NotificationWorker {
    private final NotificationRepository repository;
    private final NotificationSender sender;
    private final NotificationService service;


    @Scheduled(fixedDelay = 5000)
    public void processPendingNotifications(){
        List<Notification> lockedNotifications = service.fetchAndMarkInProgress();
        if (lockedNotifications.isEmpty()){
            return;
        }
        log.info("Захвачено {} задач. Передаю в ассинхронную отправку...", lockedNotifications.size());


        for(Notification notification : lockedNotifications){
           sender.send(notification.getId());
        }
    }

    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void recoverStuckNotifications(){
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<Notification> stuckList = repository.findByStatusAndUpdatedAtBefore(NotificationStatus.PROGRESSING, threshold);

        if(stuckList.isEmpty()){
            return;
        }
        for(Notification notification : stuckList){
            notification.setStatus(NotificationStatus.PENDING);
        }
        repository.saveAll(stuckList);
        log.warn("Восстановлено {} зависших задач из PROGRESSING в PENDING", stuckList.size());
    }
}
