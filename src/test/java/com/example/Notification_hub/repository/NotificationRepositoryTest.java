package com.example.Notification_hub.repository;

import com.example.Notification_hub.entity.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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

    @Autowired // Внедряем реальный репозиторий, никаких моков!
    private NotificationRepository repository;

    @Test
    void shouldFindTop10PendingNotifications() {

        Notification n1 = new Notification();
        n1.setRecipient("test1@mail.com");
        n1.setMessage("msg1");
        n1.setChannel("EMAIL");
        n1.setStatus("PENDING");

        Notification n2 = new Notification();
        n2.setRecipient("test2@mail.com");
        n2.setMessage("msg2");
        n2.setChannel("EMAIL");
        n2.setStatus("SENT");

        repository.save(n1);
        repository.save(n2);


        List<Notification> pendingList = repository.findTop10ByStatusOrderByCreatedAtAsc("PENDING");

        assertEquals(1, pendingList.size(), "Должно найтись только одно уведомление со статусом PENDING");
        assertEquals("test1@mail.com", pendingList.get(0).getRecipient());
    }
}