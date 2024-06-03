package com.buaa.aidraw.model.request;

import lombok.Data;

@Data
public class SaveElementRequest {
    private String fileName;
    private String filePath;
    private boolean isPublic;
    private String pngPath;

}
