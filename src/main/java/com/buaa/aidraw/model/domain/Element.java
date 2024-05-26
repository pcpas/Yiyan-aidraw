package com.buaa.aidraw.model.domain;

import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Data
public class Element {
    private String id;
    private String userId;
    private String elementName;
    private String elementUrl;
    private String folderId;
    private String prompt;
    private boolean isDelete;
    private boolean isPublic;
    private Timestamp createTime;

    public Map<String,Object> toDict(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("userId", userId);
        map.put("elementname", elementName);
        map.put("elementUrl", elementUrl);
        map.put("folderId", folderId);
        map.put("prompt", prompt);
        map.put("isDelete", isDelete);
        map.put("isPublic", isPublic);
        map.put("createTime", createTime);
        return map;
    }
}
