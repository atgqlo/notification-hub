package com.example.Notification_hub.repository;


import com.example.Notification_hub.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    public List<Notification> findTop10ByStatusOrderByCreatedAtAsc(String status);


}
