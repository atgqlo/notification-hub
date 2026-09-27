package com.example.Notification_hub.repository;

import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.entity.NotificationChannel;
import com.example.Notification_hub.entity.NotificationStatus;
import com.example.Notification_hub.worker.NotificationWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "MAIL_USERNAME=dummy@mail.com",
        "MAIL_PASSWORD=dummy_password"
})
@Testcontainers
class NotificationRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private NotificationRepository repository;

    @MockitoBean
    private NotificationWorker notificationWorker;

    @Test
    @Transactional
    void shouldFindTop10PendingNotifications() {
        Notification n1 = new Notification();
        n1.setRecipient("test1@mail.com");
        n1.setMessage("msg1");
        n1.setChannel(NotificationChannel.EMAIL);
        n1.setStatus(NotificationStatus.PENDING);

        Notification n2 = new Notification();
        n2.setRecipient("test2@mail.com");
        n2.setMessage("msg2");
        n2.setChannel(NotificationChannel.EMAIL);
        n2.setStatus(NotificationStatus.SENT);

        repository.save(n1);
        repository.save(n2);

        List<Notification> pendingList = repository.findTop10ByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING);

        assertEquals(1, pendingList.size(), "Должно найтись только одно уведомление со статусом PENDING");
        assertEquals("test1@mail.com", pendingList.get(0).getRecipient());
    }
}