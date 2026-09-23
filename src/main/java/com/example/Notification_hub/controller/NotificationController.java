package com.example.Notification_hub.controller;


import com.example.Notification_hub.entity.Notification;
import com.example.Notification_hub.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;


    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody Notification notification){
        Notification savedNotification = service.createNotification(notification);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id){
        Notification notification = service.getNotificationByID(id);
        return ResponseEntity.ok(notification);
    }

    @GetMapping
    public ResponseEntity<Page<Notification>> getAllNotifications(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size){
        Page<Notification> notifications = service.getAllNotifications(page, size);
        return ResponseEntity.ok(notifications);
    }





}
