package com.example.medjool.repository;

import com.example.medjool.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** * Repository interface for managing Notification entities.
 * Provides methods to find notifications by their read status and check existence by content.
 */


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByRead(boolean read);
    boolean existsByContent(String content);
}
