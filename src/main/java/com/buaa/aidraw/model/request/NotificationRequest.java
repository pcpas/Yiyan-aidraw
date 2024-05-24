package com.buaa.aidraw.model.request;

import com.buaa.aidraw.model.domain.Notification;
import lombok.Data;
import java.util.List;

@Data
public class NotificationRequest {
    private Notification notification;
    private List<String> userIds;
}
