package com.example.medjool.controller;


import com.example.medjool.dto.NotificationResponseDto;
import com.example.medjool.services.implementation.AlertServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** * AlertController handles requests related to user alerts.
 * It provides endpoints to retrieve all alerts and mark all alerts as read.
 */
@RestController
@RequestMapping("api/alert")
public class AlertController {
    private final AlertServiceImpl alertService;

    public AlertController(AlertServiceImpl alertService) {
        this.alertService = alertService;
    }


    @GetMapping("")
    public List<NotificationResponseDto> getAlerts(){
        return alertService.getAllAlerts();
    }


    @PutMapping("/read_all")
    public void readAll(){
        alertService.markAllAsRead();
    }
}
