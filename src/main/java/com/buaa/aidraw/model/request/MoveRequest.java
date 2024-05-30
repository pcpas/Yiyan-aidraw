package com.buaa.aidraw.model.request;

import lombok.Data;

@Data
public class MoveRequest {
    private String id;
    private String folderId;
    private String type;
}
