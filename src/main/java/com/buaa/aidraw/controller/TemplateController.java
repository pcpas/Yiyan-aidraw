package com.buaa.aidraw.controller;

import com.buaa.aidraw.model.domain.Folder;
import com.buaa.aidraw.model.domain.Project;
import com.buaa.aidraw.model.domain.Template;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.entity.FileResponse;
import com.buaa.aidraw.model.entity.ListResponse;
import com.buaa.aidraw.model.request.IdRequest;
import com.buaa.aidraw.service.FolderService;
import com.buaa.aidraw.service.TemplateService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/template")
public class TemplateController {
    @Resource
    TemplateService templateService;
    @Resource
    FolderService folderService;

    @GetMapping("/my")
    public ResponseEntity<ListResponse> myTemplate(HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Template> templateList = templateService.getTemplateBuUserId(userId);
        ListResponse listResponse = new ListResponse();
        listResponse.setList(templateList);

        return ResponseEntity.ok(listResponse);
    }

    @GetMapping("/data")
    public ResponseEntity<FileResponse> getTemplate(@RequestBody IdRequest idRequest, HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();
        String id = String.valueOf(idRequest.getId());

        Template template = templateService.getTemplateById(id);
        String file = template.getTemplateUrl();

        FileResponse fileResponse = new FileResponse();
        fileResponse.setFile(file);

        return ResponseEntity.ok(fileResponse);
    }

    @GetMapping("folder")
    public ResponseEntity<List<Folder>> folder(HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Folder> folderList = folderService.getFolders(userId, 3);
        return ResponseEntity.ok(folderList);
    }

    @GetMapping("/trash")
    public ResponseEntity<List<Template>> trashElement(HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Template> templateList = templateService.getTrashTemplate(userId);
        return ResponseEntity.ok(templateList);
    }
}
