package com.example.Notification_hub.DTO;

import com.example.Notification_hub.entity.NotificationChannel;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNotificationRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email
        String recipient,

        @NotBlank(message = "Сообщение не может быть пустым")
        @Size(min = 1, max = 500)
        String message,


        @NotNull(message = "Канал связи обязателен")
        NotificationChannel channel
){}
