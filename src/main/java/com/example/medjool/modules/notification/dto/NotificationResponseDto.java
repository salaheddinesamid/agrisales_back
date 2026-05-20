package com.example.medjool.modules.notification.dto;

import com.example.medjool.modules.notification.model.Notification;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDto {

    private Long id;
    private LocalDateTime date;
    private String content;
    private boolean read;

    public NotificationResponseDto(Notification notification) {
        this.id = notification.getId();
        this.date = notification.getDate();
        this.content = notification.getContent();
        this.read = notification.isRead();
    }
}
