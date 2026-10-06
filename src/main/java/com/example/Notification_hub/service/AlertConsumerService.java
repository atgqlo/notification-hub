package com.example.Notification_hub.service;


import com.example.Notification_hub.DTO.AlertMessage;
import com.example.Notification_hub.DTO.CreateNotificationRequest;
import com.example.Notification_hub.entity.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertConsumerService {

    private final NotificationService service;
    @KafkaListener(topics = "url_alerts", groupId = "notification-group")
    public void consumeAlert(AlertMessage alert){
        log.info("Получен алер из kafka!");

        try{
            CreateNotificationRequest request = new CreateNotificationRequest(
                    alert.recipient(), "Внимение! " + alert.url() + " " + alert.message(), alert.channel());

            service.createNotification(request);
            log.info("Уведомление успешно сохранено в бд для отправки");
        }catch (Exception e){
            log.error("Ошибка при обработке аллерта из kafka: {}", e.getMessage());
        }
    }
}
