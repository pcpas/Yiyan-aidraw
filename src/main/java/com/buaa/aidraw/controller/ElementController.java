package com.buaa.aidraw.controller;

import com.buaa.aidraw.config.OSSConfig;
import com.buaa.aidraw.exception.BaseException;
import com.buaa.aidraw.model.domain.Element;
import com.buaa.aidraw.model.domain.Folder;
import com.buaa.aidraw.model.domain.Template;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.entity.*;
import com.buaa.aidraw.model.request.GenerateRequest;
import com.buaa.aidraw.model.request.SaveElementRequest;
import com.buaa.aidraw.model.request.StringRequest;
import com.buaa.aidraw.model.request.ValueRequest;
import com.buaa.aidraw.service.*;
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    @Resource
    ElasticSearchService elasticSearchService;

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
        String name = "element";
        if(Objects.equals(fileName, "svg")){
            name = name + ".svg";
        } else if(Objects.equals(fileName, "png")){
            name = name + ".png";
        }
        System.out.println(fileName);
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
        String pngPath = saveElementRequest.getPngPath();
        System.out.println(filepath);
        boolean isPublic = saveElementRequest.isPublic();
        String prompt = openAIService.getImagePrompt(pngPath);

        elementService.addElement(userId, fileName, prompt, isPublic, filepath, pngPath);
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

    @PostMapping("/search")
    public ResponseEntity<ObjectListResponse> searchTemplate(@RequestBody ValueRequest valueRequest, HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String value = valueRequest.getValue();
        int pageNo = valueRequest.getPageNo();
        List<Element> list = elasticSearchService.searchElement(value, pageNo, 20);
        ObjectListResponse objectListResponse = new ObjectListResponse();
        objectListResponse.setElementList(list);
        return ResponseEntity.ok(objectListResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<ElementFolderResponse> allTemplate(HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Element> zeroList = elementService.getElementsByFolderId(userId, "1");

        ElementFolderResponse elementFolderResponse = new ElementFolderResponse();
        ElementFolder elementFolder_0 = new ElementFolder();
        elementFolder_0.setList(zeroList);
        elementFolder_0.setFolderName("默认文件夹");
        elementFolder_0.setId("1");

        List<ElementFolder> elementFolderList = new ArrayList<>();
        elementFolderList.add(elementFolder_0);

        List<Folder> folderList = folderService.getFolders(userId, 1);

        for(Folder folder: folderList){
            ElementFolder elementFolder = new ElementFolder();
            elementFolder.setId(folder.getId());
            elementFolder.setFolderName(folder.getFolderName());

            List<Element> templateList = elementService.getElementsByFolderId(userId, folder.getId());
            elementFolder.setList(templateList);

            elementFolderList.add(elementFolder);
        }

        elementFolderResponse.setData(elementFolderList);

        return ResponseEntity.ok(elementFolderResponse);
    }
}