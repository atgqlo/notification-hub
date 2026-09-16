package com.example.Notification_hub.exception;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(String message){
        super(message);
    }
}
