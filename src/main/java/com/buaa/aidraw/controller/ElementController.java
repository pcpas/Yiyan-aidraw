package com.buaa.aidraw.controller;

import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.entity.StringResponse;
import com.buaa.aidraw.model.request.GenerateRequest;
import com.buaa.aidraw.service.ElementService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/element")
public class ElementController {
    @Resource
    ElementService elementService;

    @PostMapping("/generate")
    public ResponseEntity<StringResponse> generateElement(@RequestBody GenerateRequest generateRequest, HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        String prompt = generateRequest.getPrompt();
        String type = generateRequest.getType();
//        System.out.println("generate");

        String elementUrl = ai_generate(prompt, type);
        StringResponse stringResponse = new StringResponse();
        stringResponse.setRes(elementUrl);

        return ResponseEntity.ok(stringResponse);
    }

//    @PostMapping("/save")
//    public ResponseEntity<String> saveElement(@RequestBody Element element, HttpServletRequest httpServletRequest){
//        User user = (User) httpServletRequest.getAttribute("user");
//
//    }

    private String ai_generate(String prompt, String type){
        String address = "here" + prompt + type;
        return  address;
    }

}
