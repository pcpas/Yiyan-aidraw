package com.buaa.aidraw.model.entity;

import lombok.Data;

@Data
public class SaveElementResponse {
    private String fileName;
    private String filePath;

    public SaveElementResponse(String filename, String filepath) {
        this.fileName = filename;
        this.filePath = filepath;
    }
}
