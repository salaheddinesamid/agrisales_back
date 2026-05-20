package com.example.medjool.modules.notification.service;


import com.example.medjool.modules.notification.dto.NotificationResponseDto;

import java.util.List;

public interface AlertService {

    void newAlert(String content);
    List<NotificationResponseDto> getAllAlerts();
    void markAllAsRead();
    void markAsRead(Long id);
    boolean isExists(String content);
}
