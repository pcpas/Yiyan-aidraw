package com.buaa.aidraw.model.request;

import lombok.Data;

@Data
public class CreateRequest {
    private String name;
    private boolean isPublic;
}
