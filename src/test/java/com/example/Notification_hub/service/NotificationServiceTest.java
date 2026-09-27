package com.example.Notification_hub.service;

import com.example.Notification_hub.DTO.CreateNotificationRequest;
import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.entity.NotificationChannel;
import com.example.Notification_hub.entity.NotificationStatus;
import com.example.Notification_hub.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void createNotification_ShouldSetPendingStatusAndSave() {
        CreateNotificationRequest input = new CreateNotificationRequest("test@mail.com", "message", NotificationChannel.EMAIL);


        when(repository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(1L);
            return n;
        });

        Notification result = notificationService.createNotification(input);

        assertNotNull(result.getId(), "ID не должен быть null");
        assertEquals(NotificationStatus.PENDING, result.getStatus(), "Статус должен быть PENDING");
        assertEquals("test@mail.com", result.getRecipient());

        verify(repository, times(1)).save(any(Notification.class));
    }
}