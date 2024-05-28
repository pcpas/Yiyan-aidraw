package com.buaa.aidraw.controller;

import com.buaa.aidraw.model.domain.Notification;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.entity.StringResponse;
import com.buaa.aidraw.model.request.NotificationRequest;
import com.buaa.aidraw.model.request.StringRequest;
import com.buaa.aidraw.service.NotificationService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @PostMapping("/publish")
    public ResponseEntity<StringResponse> createNotification(@RequestBody NotificationRequest request) {
        notificationService.createNotification(request.getNotification(), request.getUserIds());
        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @GetMapping("/getAllUnreadNotifications")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        List<Notification> res =  notificationService.getUnreadNotificationsByUserId(user.getId());
        if(res == null) res = new ArrayList<>();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/markAsRead")
    public ResponseEntity<StringResponse> markNotificationAsRead(@RequestBody StringRequest request, HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        System.out.println(request.getValue());
        notificationService.markNotificationAsRead(user.getId(), request.getValue());
        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @PostMapping("/markAllAsRead")
    public ResponseEntity<StringResponse> markAllNotificationsAsReadByUserId(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        notificationService.markAllNotificationsAsReadByUserId(user.getId());
        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @GetMapping("/getAllNotifications")
    public ResponseEntity<List<Notification>> getAllNotificationsByUserId(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        List<Notification> res = notificationService.getAllNotificationsWithReadStatusByUserId(user.getId());
        if(res == null) res = new ArrayList<>();
        return ResponseEntity.ok(res);
    }
}