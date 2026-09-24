package com.example.Notification_hub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "Email не может быть пустым")
    @Email
    @Column(nullable = false)
    private String recipient;

    @NotBlank(message = "Сообщение не может быть пустым")
    @Column(nullable = false)
    @Size(min = 1, max = 500)
    private String message;


    @NotBlank(message = "Канал связи обязателен")
    @Column(nullable = false)
    private String channel;


    @Column(nullable = false)
    private String status;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

}
