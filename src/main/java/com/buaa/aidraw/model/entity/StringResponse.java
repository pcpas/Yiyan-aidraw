package com.buaa.aidraw.model.entity;

import lombok.Data;

@Data
public class StringResponse {
    private String res;

    public StringResponse(){}

    public StringResponse(String msg){
        res = msg;
    }
}
