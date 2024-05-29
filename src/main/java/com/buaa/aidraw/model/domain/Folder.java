package com.buaa.aidraw.model.domain;

import lombok.Data;

@Data
public class Folder {
    private String id;
    private String userId;
    private String folderName;
    private int type;
    private boolean isDefault;
}
