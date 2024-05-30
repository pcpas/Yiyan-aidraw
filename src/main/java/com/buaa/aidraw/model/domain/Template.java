package com.buaa.aidraw.model.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Template {
    private String id;
    private String userId;
    private String templateName;
    private String templateUrl;
    private String fileUrl;
    private boolean isDelete;
    private boolean isPublic;
    private Date createTime;
    private String folderId;
}
