package com.buaa.aidraw.controller;

import com.buaa.aidraw.exception.BaseException;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.entity.StringResponse;
import com.buaa.aidraw.model.request.GenerateRequest;
import com.buaa.aidraw.service.BaiduAIService;
import com.buaa.aidraw.service.ElementService;
import com.buaa.aidraw.service.OpenAIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/element")
public class ElementController {
    @Resource
    ElementService elementService;
    @Resource
    OpenAIService openAIService;
    @Resource
    BaiduAIService baiduAIService;

    /***
     * 为图片生成文本描述（测试用）
     * @param image 图片Url
     */
    @GetMapping("/getImagePrompt")
    public ResponseEntity<StringResponse> getImagePrompt(@RequestBody String image, HttpServletRequest httpServletRequest) throws IOException {
        String prompt = openAIService.getImagePrompt(image);
        return ResponseEntity.ok(new StringResponse(prompt));
    }


    @PostMapping("/generate")
    public ResponseEntity<StringResponse> generateImage(@RequestBody GenerateRequest generateRequest, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        User user = (User) httpServletRequest.getAttribute("user");
        String type = generateRequest.getType();
        // type: "png" "svg" "font"
        String imageUrl = null;
        switch (type) {
            case "png":
                String taskId = baiduAIService.generateImage(generateRequest.getPrompt(), generateRequest.getWidth(), generateRequest.getHeight(), generateRequest.getStyle());
                int waitTime = 5000;
                while (true) {
                    // 调用 getImage 方法获取任务状态
                    String res = baiduAIService.getImage(taskId);
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(res);
                    Integer taskStatus = rootNode.path("data").path("status").asInt();
                    System.out.println(taskStatus);
                    System.out.println(taskId);
                    // 检查任务状态
                    if (taskStatus == 1) {
                        imageUrl = rootNode.path("data").path("img").asText();
                        break;
                    }else {
                        try {
                            Thread.sleep(waitTime);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                            throw new BaseException("未知错误！");
                        }
                    }
                }
                break;
            case "svg", "font":
                break;
        }
        return ResponseEntity.ok(new StringResponse(imageUrl));
    }
}