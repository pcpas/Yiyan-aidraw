package com.buaa.aidraw.model.request;

import lombok.Data;

@Data
public class StringRequest {
    String value;
    public StringRequest(){

    }

    public StringRequest(String str){
        this.value = str;
    }
}
