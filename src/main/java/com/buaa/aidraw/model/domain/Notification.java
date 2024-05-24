package com.buaa.aidraw.model.domain;

import lombok.Data;
import java.sql.Timestamp;
@Data
public class Notification {
    private String id;
    private String title;
    private String content;
    private String type;
    private Timestamp createdAt;
    private Timestamp sentAt;
    private Timestamp readAt;
    private boolean isRead;
}