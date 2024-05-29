package com.buaa.aidraw.model.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Project {
    private String id;
    private String userId;
    private String projectName;
    private String projectUrl;
    private boolean isDelete;
    private boolean isPublic;
    private String fileUrl;
    private Date editTime;
    private String folderId;
}
