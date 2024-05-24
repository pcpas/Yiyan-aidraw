package com.buaa.aidraw.model.domain;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class UserNotification {
    private String id;
    private String userId;
    private String notificationId;
    private String status;
    private Timestamp createdAt;
    private Timestamp readAt;
}
