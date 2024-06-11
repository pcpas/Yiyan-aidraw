package com.buaa.aidraw.controller;

import com.buaa.aidraw.model.domain.Notification;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.entity.StringResponse;
import com.buaa.aidraw.model.request.NotificationRequest;
import com.buaa.aidraw.model.request.StringRequest;
import com.buaa.aidraw.service.NotificationService;
import com.buaa.aidraw.service.RedisService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Resource
    private NotificationService notificationService;
    @Resource
    private RedisService redisService;

    @PostMapping("/publish")
    public ResponseEntity<StringResponse> createNotification(@RequestBody NotificationRequest request) {
        Notification notification = request.getNotification();
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        notification.setCreatedAt(now);
        notification.setSentAt(now);
        notificationService.createNotification(notification, request.getUserIds());
        // Cache the notification data
        for (String userId : request.getUserIds()) {
            String cacheKey = "notifications:user:" + userId;
            List<Notification> notifications = redisService.getList(cacheKey, Notification.class);
            if (notifications == null) {
                notifications = new ArrayList<>();
            } else {
                notifications = new ArrayList<>(notifications); // Make sure it's a mutable list
            }
            notifications.add(notification);
            redisService.add(cacheKey, notifications);
        }

        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @PostMapping("/markAsRead")
    public ResponseEntity<StringResponse> markNotificationAsRead(@RequestBody StringRequest request, HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        notificationService.markNotificationAsRead(user.getId(), request.getValue());

        String cacheKey = "notifications:user:" + user.getId();
        List<Notification> notifications = redisService.getList(cacheKey, Notification.class);
        if (notifications != null) {
            for (Notification notification : notifications) {
                if (notification.getId().equals(request.getValue())) {
                    notification.setRead(true);
                    notification.setReadAt(now);
                    break;
                }
            }
            redisService.add(cacheKey, notifications);
        }

        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @PostMapping("/markAllAsRead")
    public ResponseEntity<StringResponse> markAllNotificationsAsReadByUserId(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        notificationService.markAllNotificationsAsReadByUserId(user.getId());
        String cacheKey = "notifications:user:" + user.getId();
        List<Notification> notifications = redisService.getList(cacheKey, Notification.class);
        if (notifications != null) {
            for (Notification notification : notifications) {
                notification.setRead(true);
                notification.setReadAt(now);
            }
            redisService.add(cacheKey, notifications);
        }
        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @GetMapping("/getAllUnreadNotifications")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");

        String cacheKey = "notifications:user:" + user.getId();
        List<Notification> notifications = redisService.getList(cacheKey, Notification.class);
        if (notifications == null) {
            // Fetch from database if not in cache
            notifications =  notificationService.getUnreadNotificationsByUserId(user.getId());
            redisService.add(cacheKey, notifications);
        }
        if(notifications == null) notifications = new ArrayList<>();
        System.out.println(notifications);
        return ResponseEntity.ok(notifications.stream().filter(notification -> !notification.isRead()).collect(Collectors.toList()));
    }

    @GetMapping("/getAllNotifications")
    public ResponseEntity<List<Notification>> getAllNotificationsByUserId(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");

        String cacheKey = "notifications:user:" + user.getId();
        List<Notification> notifications = redisService.getList(cacheKey, Notification.class);
        if (notifications == null) {
            notifications = notificationService.getAllNotificationsWithReadStatusByUserId(user.getId());
            redisService.add(cacheKey, notifications);
        }
        if(notifications == null) notifications = new ArrayList<>();
        return ResponseEntity.ok(notifications);
    }
}