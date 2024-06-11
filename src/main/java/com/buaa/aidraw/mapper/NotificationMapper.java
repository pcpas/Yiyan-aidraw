package com.buaa.aidraw.mapper;

import com.buaa.aidraw.model.domain.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Insert("INSERT INTO notifications(title, content, type, created_at, sent_at) " +
            "VALUES(#{title}, #{content}, #{type},  #{createdAt}, #{sentAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertNotification(Notification notification);

}