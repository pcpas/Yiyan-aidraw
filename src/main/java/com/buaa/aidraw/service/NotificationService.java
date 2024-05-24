package com.buaa.aidraw.service;


import com.buaa.aidraw.mapper.NotificationMapper;
import com.buaa.aidraw.mapper.UserNotificationMapper;
import com.buaa.aidraw.model.domain.Notification;
import com.buaa.aidraw.model.domain.UserNotification;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private UserNotificationMapper userNotificationMapper;

    @Transactional
    public void createNotification(Notification notification, List<String> userIds) {
        notificationMapper.insertNotification(notification);

        for (String userId : userIds) {
            UserNotification userNotification = new UserNotification();
            userNotification.setUserId(userId);
            userNotification.setNotificationId(notification.getId());
            userNotification.setStatus("unread");
            userNotificationMapper.insertUserNotification(userNotification);
        }
    }

    public List<Notification> getUnreadNotificationsByUserId(String userId) {
        return userNotificationMapper.getUnreadNotificationsByUserId(userId);
    }

    public List<Notification> getAllNotificationsWithReadStatusByUserId(String userId) {
        return userNotificationMapper.getAllNotificationsWithReadStatusByUserId(userId);
    }

    public void markNotificationAsRead(String userId, String notificationId) {
        userNotificationMapper.markNotificationAsRead(userId, notificationId);
    }

    @Transactional
    public void markAllNotificationsAsReadByUserId(String userId) {
        userNotificationMapper.markAllNotificationsAsReadByUserId(userId);
    }
}