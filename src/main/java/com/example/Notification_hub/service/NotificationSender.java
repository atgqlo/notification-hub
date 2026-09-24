package com.example.Notification_hub.service;


import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSender {
    private final NotificationRepository repository;
    private final JavaMailSender mailSender;


    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async("notificationExecutor")
    public void send(Notification notification) {
        try{
            log.info("Поток [{}] начинает отправку письма на {}", Thread.currentThread().getName(), notification.getRecipient());

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(notification.getRecipient());
            mailMessage.setSubject("Уведомление от NotificationHub");
            mailMessage.setText(notification.getMessage());

            mailSender.send(mailMessage);

            notification.setStatus("SENT");
            repository.save(notification);

            log.info("Письмо на {} успешно отправлено", notification.getRecipient());
        }catch (MailException e){
            log.error("Ошибка сети при отправке на {}", notification.getRecipient(), e);
            int maxRetries = 3;
            if (notification.getRetryCount() < maxRetries){
                notification.setRetryCount(notification.getRetryCount() + 1);
                notification.setStatus("PENDING");
                log.warn("Попытка {} из {}. Задача возвращена в PENDING", notification.getRetryCount(), maxRetries);
            }else{
                notification.setStatus("FAILED");
                log.error("Исчерпан лимит попыток для {}", notification.getRecipient());
            }
            repository.save(notification);
        }

    }
}
