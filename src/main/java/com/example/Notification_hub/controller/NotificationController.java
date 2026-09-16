package com.example.Notification_hub.controller;


import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;


    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification){
        Notification savedNotification = service.createNotification(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
    }
}
