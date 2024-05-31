package com.buaa.aidraw.controller;

import com.buaa.aidraw.config.OSSConfig;
import com.buaa.aidraw.exception.BaseException;
import com.buaa.aidraw.model.domain.Element;
import com.buaa.aidraw.model.domain.Folder;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.entity.FolderListResponse;
import com.buaa.aidraw.model.entity.ObjectListResponse;
import com.buaa.aidraw.model.entity.SaveElementResponse;
import com.buaa.aidraw.model.entity.StringResponse;
import com.buaa.aidraw.model.request.GenerateRequest;
import com.buaa.aidraw.model.request.SaveElementRequest;
import com.buaa.aidraw.model.request.StringRequest;
import com.buaa.aidraw.service.BaiduAIService;
import com.buaa.aidraw.service.ElementService;
import com.buaa.aidraw.service.FolderService;
import com.buaa.aidraw.service.OpenAIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/element")
public class ElementController {
    @Resource
    ElementService elementService;
    @Resource
    FolderService folderService;
    @Resource
    OpenAIService openAIService;
    @Resource
    BaiduAIService baiduAIService;
    @Resource
    OSSConfig ossConfig;

    /***
     * 为图片生成文本描述（测试用）
     * @param image 图片Url
     */
    @GetMapping("/getImagePrompt")
    public ResponseEntity<StringResponse> getImagePrompt(@RequestBody String image, HttpServletRequest httpServletRequest) throws IOException {
        String prompt = openAIService.getImagePrompt(image);
        return ResponseEntity.ok(new StringResponse(prompt));
    }

    /***
     * 生成素材图片
     * @param generateRequest 生成参数
     * @return 素材的Url
     */
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

    @GetMapping("/upload")
    public ResponseEntity<SaveElementResponse> uploadElement(@RequestPart("file") MultipartFile image,
                                                             @RequestPart("fileName") String fileName,
                                                             HttpServletRequest httpServletRequest) throws IOException {
        if(ObjectUtils.isEmpty(image) || image.getSize() <= 0){
            return ResponseEntity.badRequest().body(new SaveElementResponse(null, null));
        }
        String name = fileName + ".svg";
        String res = ossConfig.upload(image, "element", name);
        if (res != null){
            return ResponseEntity.ok(new SaveElementResponse(fileName, res));
        } else {
            return ResponseEntity.badRequest().body(new SaveElementResponse(null, null));
        }
    }

    @PostMapping("/save")
    public ResponseEntity<StringResponse> saveElement(@RequestBody SaveElementRequest saveElementRequest, HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();
        String fileName = saveElementRequest.getFileName();
        String filepath = saveElementRequest.getFilePath();
        boolean isPublic = saveElementRequest.isPublic();
        String prompt = openAIService.getImagePrompt(filepath);

        elementService.addElement(userId, fileName, prompt, isPublic, filepath);
        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @GetMapping("/my")
    public ResponseEntity<ObjectListResponse> myElement(HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Element> elementList = elementService.getElementsByUserId(userId);
        ObjectListResponse objectListResponse = new ObjectListResponse();
        objectListResponse.setElementList(elementList);
        return ResponseEntity.ok(objectListResponse);
    }

    @GetMapping("/folder")
    public ResponseEntity<FolderListResponse> folder(HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Folder> folderList = folderService.getFolders(userId, 1);
        FolderListResponse folderListResponse = new FolderListResponse();
        folderListResponse.setFolderList(folderList);
        return ResponseEntity.ok(folderListResponse);
    }

    @GetMapping("/trash")
    public ResponseEntity<ObjectListResponse> trashElement(HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Element> elementList = elementService.getTrashElements(userId);
        ObjectListResponse objectListResponse = new ObjectListResponse();
        objectListResponse.setElementList(elementList);
        return ResponseEntity.ok(objectListResponse);
    }

    @PostMapping("/get")
    public ResponseEntity<StringResponse> getElement(@RequestBody StringRequest stringRequest, HttpServletRequest httpServletRequest){
        String elementId = stringRequest.getValue();
//        System.out.println(elementId);

        Element element = elementService.getElementById(elementId);
        String file = element.getElementUrl();
        return ResponseEntity.ok(new StringResponse(file));
    }
}