package com.buaa.aidraw.model.entity;

import lombok.Data;

@Data
public class ImgUrlResponse {
    private String imgUrl;

    public ImgUrlResponse(String imgUrl) {

        this.imgUrl = imgUrl;
    }
}
