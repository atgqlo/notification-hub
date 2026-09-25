package com.example.Notification_hub.service;


import com.example.Notification_hub.DTO.CreateNotificationRequest;
import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.exception.NotificationNotFoundException;
import com.example.Notification_hub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;

    public Notification createNotification(CreateNotificationRequest request){
        log.info("Создание нового уведомления для : {}", request.recipient());

      Notification notification = new Notification();

      notification.setStatus("PENDING");
      notification.setRecipient(request.recipient());
      notification.setChannel(request.channel());
      notification.setMessage(request.message());

        return repository.save(notification);
    }

    public Notification getNotificationByID(Long ID){
        log.info("Получение уведомления по id");

        return repository.findById(ID).orElseThrow(() -> new NotificationNotFoundException("Уведомление с ID " + ID + " не найдено"));

    }

    public Page<Notification> getAllNotifications(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAll(pageable);
    }



}
