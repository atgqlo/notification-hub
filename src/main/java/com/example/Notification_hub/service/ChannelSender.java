package com.example.Notification_hub.service;

import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.entity.NotificationChannel;

public interface ChannelSender {
    void send(Notification notification);
    NotificationChannel getChannel();

}
