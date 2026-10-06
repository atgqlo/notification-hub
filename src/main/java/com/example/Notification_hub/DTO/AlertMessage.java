package com.example.Notification_hub.DTO;

import com.example.Notification_hub.entity.NotificationChannel;

public record AlertMessage(
        Long id,
        String url,
        String message,
        String recipient,
        NotificationChannel channel
) {}
