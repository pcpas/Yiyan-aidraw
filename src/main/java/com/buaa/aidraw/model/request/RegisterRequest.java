package com.buaa.aidraw.model.request;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String avatar_url;
    private String salt;
}