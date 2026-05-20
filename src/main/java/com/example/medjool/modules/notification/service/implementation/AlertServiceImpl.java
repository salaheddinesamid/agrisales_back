package com.example.medjool.modules.notification.service.implementation;

import com.example.medjool.modules.notification.dto.NotificationResponseDto;
import com.example.medjool.modules.notification.model.Notification;
import com.example.medjool.modules.notification.service.AlertService;
import com.example.medjool.modules.user_management.service.implementation.repository.NotificationRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertServiceImpl implements AlertService {
    private final NotificationRepository notificationRepository;


    public AlertServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void newAlert(String content) {
        Notification notification = new Notification();
        LocalDateTime now = LocalDateTime.now();
        if(!notificationRepository.existsByContent(content)){
            notification.setDate(now);
            notification.setContent(content);
            notification.setRead(false);
            notificationRepository.save(notification);
        }
    }


    @Override
    public List<NotificationResponseDto> getAllAlerts() {
        List<Notification> alerts = notificationRepository.findAllByRead(false);
        return alerts.stream().map(NotificationResponseDto::new).toList();
    }


    @CacheEvict(value = "alerts", allEntries = true)
    @Override
    public void markAllAsRead() {
        for(Notification notification : notificationRepository.findAll()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public void markAsRead(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public boolean isExists(String content) {
        return notificationRepository.existsByContent(content);
    }
}
