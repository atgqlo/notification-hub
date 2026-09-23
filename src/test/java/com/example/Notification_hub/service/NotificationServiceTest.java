package com.example.Notification_hub.service;

import com.example.Notification_hub.entity.Notification;
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

        Notification input = new Notification();
        input.setRecipient("test@mail.com");
        input.setMessage("Привет!");
        input.setChannel("EMAIL");

        Notification saved = new Notification();
        saved.setId(1L);
        saved.setRecipient("test@mail.com");
        saved.setStatus("PENDING");

        when(repository.save(any(Notification.class))).thenReturn(saved);

        Notification result = notificationService.createNotification(input);


        assertNotNull(result.getId(), "ID не должен быть null");
        assertEquals("PENDING", result.getStatus(), "Статус должен быть PENDING");


        verify(repository, times(1)).save(any(Notification.class));
    }
}