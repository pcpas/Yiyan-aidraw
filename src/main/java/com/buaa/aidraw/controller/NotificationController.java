package com.buaa.aidraw.controller;

import com.buaa.aidraw.model.domain.Notification;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.request.NotificationRequest;
import com.buaa.aidraw.service.NotificationService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @PostMapping("/publish")
    public ResponseEntity<String> createNotification(@RequestBody NotificationRequest request) {
        notificationService.createNotification(request.getNotification(), request.getUserIds());
        return ResponseEntity.ok("发布成功！");
    }

    @GetMapping("/getAllUnreadNotifications")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        List<Notification> res =  notificationService.getUnreadNotificationsByUserId(user.getId());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/markAsRead")
    public ResponseEntity<String> markNotificationAsRead(@RequestBody String notificationId, HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        System.out.println(notificationId);
        notificationService.markNotificationAsRead(user.getId(), notificationId);
        return ResponseEntity.ok("成功已读！");
    }

    @PostMapping("/markAllAsRead")
    public ResponseEntity<String> markAllNotificationsAsReadByUserId(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        notificationService.markAllNotificationsAsReadByUserId(user.getId());
        return ResponseEntity.ok("成功全部已读！");
    }

    @GetMapping("/getAllNotifications")
    public ResponseEntity<List<Notification>> getAllNotificationsByUserId(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        List<Notification> res = notificationService.getAllNotificationsWithReadStatusByUserId(user.getId());
        return ResponseEntity.ok(res);
    }
}