package com.buaa.aidraw.controller;

import com.buaa.aidraw.service.BaiduAIService;
import com.buaa.aidraw.service.OpenAIService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AIController {
    @Resource
    OpenAIService openAIService;
    @Resource
    BaiduAIService baiduAIService;

    @GetMapping("/getImagePrompt")
    public ResponseEntity<String> test(@RequestBody String image, HttpServletRequest httpServletRequest) throws IOException {
        String prompt = openAIService.getImagePrompt(image);
        return ResponseEntity.ok(prompt);
    }

}
