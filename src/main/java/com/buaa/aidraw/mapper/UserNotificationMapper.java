package com.buaa.aidraw.mapper;

import com.buaa.aidraw.model.domain.Notification;
import com.buaa.aidraw.model.domain.UserNotification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserNotificationMapper {

    @Insert("INSERT INTO user_notifications(user_id, notification_id, status, created_at, read_at) " +
            "VALUES(#{userId}, #{notificationId}, #{status}, #{createdAt}, #{readAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUserNotification(UserNotification userNotification);

    @Select("SELECT n.id, n.title, n.content, n.created_at, n.sent_at, n.type,  " +
            "CASE WHEN un.status = 'read' THEN true ELSE false END as isRead " +
            "FROM user_notifications un " +
            "JOIN notifications n ON un.notification_id = n.id " +
            "WHERE un.user_id = #{userId} AND un.status = 'unread'")
    List<Notification> getUnreadNotificationsByUserId(@Param("userId") String userId);

    @Update("UPDATE user_notifications SET status = 'read', read_at = NOW() " +
            "WHERE user_id = #{userId} AND notification_id = #{notificationId}")
    void markNotificationAsRead(@Param("userId") String userId, @Param("notificationId") String notificationId);

    @Update("UPDATE user_notifications SET status = 'read', read_at = NOW() " +
            "WHERE user_id = #{userId} AND status = 'unread'")
    void markAllNotificationsAsReadByUserId(@Param("userId") String userId);

    @Select("SELECT n.id, n.title, n.content, n.type, n.sent_at, un.read_at, " +
            "CASE WHEN un.status = 'read' THEN true ELSE false END as isRead " +
            "FROM user_notifications un " +
            "JOIN notifications n ON un.notification_id = n.id " +
            "WHERE un.user_id = #{userId}")
    List<Notification> getAllNotificationsWithReadStatusByUserId(@Param("userId") String userId);

}
