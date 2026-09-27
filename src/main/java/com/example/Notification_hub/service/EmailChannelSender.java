package com.example.Notification_hub.service;


import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.entity.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailChannelSender implements ChannelSender {


    private final JavaMailSender mailSender;


    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void send(Notification notification) {
        log.info("Отправка email для {}", notification.getRecipient());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(notification.getRecipient());
        message.setSubject("Новое уведомление от NotificationHub");
        message.setText(notification.getMessage());

        mailSender.send(message);
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }
}
