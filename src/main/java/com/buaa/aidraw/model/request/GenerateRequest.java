package com.buaa.aidraw.model.request;

import lombok.Data;

@Data
public class GenerateRequest {
    private String prompt;
    private String type;
    private String style;
    private Integer width;
    private Integer height;
}
