package com.example.Notification_hub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String channel;


    @Column(nullable = false)
    private String status;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;



}
