package com.buaa.aidraw.controller;

import co.elastic.clients.elasticsearch.core.DeleteRequest;
import com.buaa.aidraw.model.domain.*;
import com.buaa.aidraw.model.entity.ObjectListResponse;
import com.buaa.aidraw.model.entity.StringResponse;
import com.buaa.aidraw.model.request.*;
import com.buaa.aidraw.service.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/folder")
public class FolderController {
    @Resource
    private FolderService folderService;
    @Resource
    private ElementService elementService;
    @Resource
    private ProjectService projectService;
    @Resource
    private TemplateService templateService;

    @PostMapping("/new")
    public ResponseEntity<StringResponse> newFolder(@RequestBody TypeRequest typeRequest, HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String type = typeRequest.getType();
        int typeInt = transType(type);

        int res = folderService.addFolder(userId, typeInt);
        if(res > 0){
            return ResponseEntity.ok(new StringResponse("成功"));
        } else {
            return ResponseEntity.badRequest().body(new StringResponse("失败"));
        }
    }

    @PostMapping("/rename")
    public ResponseEntity<StringResponse> renameFolder(@RequestBody RenameRequest renameRequest, HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String id = renameRequest.getId();
        String name = renameRequest.getName();

        int res = folderService.updateFolder(id, name);
        if(res > 0){
            return ResponseEntity.ok(new StringResponse(name));
        } else {
            return ResponseEntity.badRequest().body(new StringResponse("更新名称失败"));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<StringResponse> deleteFolder(@RequestBody IdStringRequest idStringRequest, HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String id = idStringRequest.getId();
        Folder folder = folderService.getFolder(id);
        String folderId = folder.getId();
        int type = folder.getType();

        // 处理文件夹中元素，放入回收站+文件夹清0
        switch (type){
            case 1:
                List<Element> elementList = elementService.getElementsByFolderId(userId, folderId);
                for(Element element : elementList){
                    elementService.updateFolder(element.getId(), "0", true);
                }
                break;
            case 2:
                List<Project> projectList = projectService.getProjectsByFolderId(userId, folderId);
                for(Project project : projectList){
                    projectService.updateFolder(project.getId(), "0", true);
                }
                break;
            case 3:
                List<Template> templateList = templateService.getTemplatesByFolderId(userId, folderId);
                for(Template template : templateList){
                    templateService.updateFolder(template.getId(), "0", true);
                }
                break;
        }

        int res = folderService.deleteFolder(id);
        if(res > 0){
            return ResponseEntity.ok(new StringResponse("删除成功"));
        } else {
            return ResponseEntity.badRequest().body(new StringResponse("删除失败"));
        }
    }

    @PostMapping("/move")
    public ResponseEntity<StringResponse> moveObject(@RequestBody MoveRequest moveRequest, HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String id = moveRequest.getId();
        String folderId = moveRequest.getFolderId();
        String type = moveRequest.getType();
        int typeInt = transType(type);

        switch (typeInt){
            case 1:
                Element element = elementService.getElementById(id);
                elementService.updateFolder(element.getId(), folderId, false);
                break;
            case 2:
                Project project = projectService.getProjectById(id);
                projectService.updateFolder(project.getId(), folderId, false);
                break;
            case 3:
                Template template = templateService.getTemplateById(id);
                templateService.updateFolder(template.getId(), folderId, false);
                break;
        }

        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @PostMapping("/delItem")
    public ResponseEntity<StringResponse> delItem(@RequestBody IdTypeRequest idTypeRequest, HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String id = idTypeRequest.getId();
        String type = idTypeRequest.getType();
        int typeInt = transType(type);

        switch (typeInt){
            case 1:
                Element element = elementService.getElementById(id);
                elementService.updateFolder(element.getId(), element.getFolderId(), true);
                break;
            case 2:
                Project project = projectService.getProjectById(id);
                projectService.updateFolder(project.getId(), project.getFolderId(), true);
                break;
            case 3:
                Template template = templateService.getTemplateById(id);
                templateService.updateFolder(template.getId(), template.getFolderId(), true);
                break;
        }

        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @PostMapping("/recoverItem")
    public ResponseEntity<StringResponse> recoverItem(@RequestBody IdTypeRequest idTypeRequest, HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String id = idTypeRequest.getId();
        String type = idTypeRequest.getType();
        int typeInt = transType(type);

        switch (typeInt){
            case 1:
                Element element = elementService.getElementById(id);
                elementService.updateFolder(element.getId(), element.getFolderId(), false);
                break;
            case 2:
                Project project = projectService.getProjectById(id);
                projectService.updateFolder(project.getId(), project.getFolderId(), false);
                break;
            case 3:
                Template template = templateService.getTemplateById(id);
                templateService.updateFolder(template.getId(), template.getFolderId(), false);
                break;
        }

        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @PostMapping("/switch")
    public ResponseEntity<StringResponse> switchItem(@RequestBody IdTypeRequest idTypeRequest, HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String id = idTypeRequest.getId();
        String type = idTypeRequest.getType();
        int typeInt = transType(type);

        switch (typeInt){
            case 1:
                Element element = elementService.getElementById(id);
                elementService.updatePublic(element.getId(), !(element.isPublic()));
                break;
            case 3:
                Template template = templateService.getTemplateById(id);
                templateService.updatePublic(template.getId(), !(template.isPublic()));
                break;
        }

        return ResponseEntity.ok(new StringResponse("成功"));
    }

    @PostMapping("/get")
    public ResponseEntity<ObjectListResponse> getItem(@RequestBody IdStringRequest idStringRequest, HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String folderId = idStringRequest.getId();
        Folder folder = folderService.getFolder(folderId);

        ObjectListResponse objectListResponse = new ObjectListResponse();

        int type = folder.getType();
        switch (type){
            case 1:
                List<Element> elementList = elementService.getElementsByFolderId(userId, folderId);
                objectListResponse.setElementList(elementList);
                objectListResponse.setType("element");
                break;
            case 2:
                List<Project> projectList = projectService.getProjectsByFolderId(userId, folderId);
                objectListResponse.setProjectList(projectList);
                objectListResponse.setType("project");
                break;
            case 3:
                List<Template> templateList = templateService.getTemplatesByFolderId(userId, folderId);
                objectListResponse.setTemplateList(templateList);
                objectListResponse.setType("template");
                break;
        }

        return ResponseEntity.ok(objectListResponse);
    }

    private int transType(String type){
        int typeInt = 0;
        switch (type){
            case "element":
                typeInt = 1;
                break;
            case "project":
                typeInt = 2;
                break;
            case "template":
                typeInt = 3;
                break;
        }
        return typeInt;
    }
}
