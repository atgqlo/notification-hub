package com.example.Notification_hub.worker;


import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.repository.NotificationRepository;
import com.example.Notification_hub.service.NotificationSender;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor

public class NotificationWorker {
    private final NotificationRepository repository;
    private final NotificationSender sender;


    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void processPendingNotifications(){
        List<Notification> pendingList = repository.findTop10ByStatusOrderByCreatedAtAsc("PENDING");
        if (pendingList.isEmpty()){
            return;
        }
        log.info("Найдено {} задач со статусом PENDING. Раздаю потокам...", pendingList.size());


        for(Notification notification : pendingList){
            notification.setStatus("PROGRESSING");
            repository.save(notification);
            sender.send(notification);
        }
    }
}
