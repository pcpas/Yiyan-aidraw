package com.buaa.aidraw.controller;

import com.aliyuncs.exceptions.ClientException;
import com.buaa.aidraw.model.domain.Folder;
import com.buaa.aidraw.model.domain.Project;
import com.buaa.aidraw.model.domain.Template;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.entity.*;
import com.buaa.aidraw.model.request.IdRequest;
import com.buaa.aidraw.model.request.IdStringRequest;
import com.buaa.aidraw.model.request.ValueRequest;
import com.buaa.aidraw.service.ElasticSearchService;
import com.buaa.aidraw.service.FolderService;
import com.buaa.aidraw.service.ProjectService;
import com.buaa.aidraw.service.TemplateService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/template")
public class TemplateController {
    @Resource
    TemplateService templateService;
    @Resource
    FolderService folderService;
    @Resource
    ProjectService projectService;
    @Resource
    ElasticSearchService elasticSearchService;

    @PostMapping("/create")
    public ResponseEntity<IdResponse> createTemplate(@RequestBody IdStringRequest idStringRequest, HttpServletRequest httpServletRequest) throws ClientException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String projectId = idStringRequest.getId();
        Project project = projectService.getProjectById(projectId);

        String TemplateId = templateService.createTemplate(project);
        IdResponse idResponse = new IdResponse();
        idResponse.setId(TemplateId);
        return ResponseEntity.ok(idResponse);
    }


    @GetMapping("/my")
    public ResponseEntity<ListResponse> myTemplate(HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Template> templateList = templateService.getTemplateBuUserId(userId);
        ListResponse listResponse = new ListResponse();
        listResponse.setList(templateList);

        return ResponseEntity.ok(listResponse);
    }

    @PostMapping("/data")
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
    public ResponseEntity<FolderListResponse> folder(HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Folder> folderList = folderService.getFolders(userId, 3);
        FolderListResponse folderListResponse = new FolderListResponse();
        folderListResponse.setFolderList(folderList);
        return ResponseEntity.ok(folderListResponse);
    }

    @GetMapping("/trash")
    public ResponseEntity<ObjectListResponse> trashElement(HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Template> templateList = templateService.getTrashTemplate(userId);
        ObjectListResponse objectListResponse = new ObjectListResponse();
        objectListResponse.setTemplateList(templateList);
        return ResponseEntity.ok(objectListResponse);
    }

    @PostMapping("/search")
    public ResponseEntity<ListResponse> searchTemplate(@RequestBody ValueRequest valueRequest, HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String value = valueRequest.getValue();
        int pageNo = valueRequest.getPageNo();
        List<Template> list = elasticSearchService.searchTemplate(value, pageNo, 20);
        ListResponse listResponse = new ListResponse();
        listResponse.setList(list);
        return ResponseEntity.ok(listResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<TemplateFolderResponse> allTemplate(HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        ListResponse zero = new ListResponse();
        List<Template> zeroList = templateService.getTemplatesByFolderId(userId, "3");
        zero.setList(zeroList);

        TemplateFolderResponse templateFolderResponse = new TemplateFolderResponse();
        TemplateFolder templateFolder_0 = new TemplateFolder();
        templateFolder_0.setList(zeroList);
        templateFolder_0.setFolderName("默认文件夹");
        templateFolder_0.setId("3");

        List<TemplateFolder> templateFolderList = new ArrayList<>();
        templateFolderList.add(templateFolder_0);

        List<Folder> folderList = folderService.getFolders(userId, 3);

        for(Folder folder: folderList){
            TemplateFolder templateFolder = new TemplateFolder();
            templateFolder.setId(folder.getId());
            templateFolder.setFolderName(folder.getFolderName());

            List<Template> templateList = templateService.getTemplatesByFolderId(userId, folder.getId());
            templateFolder.setList(templateList);

            templateFolderList.add(templateFolder);
        }

        templateFolderResponse.setData(templateFolderList);

        return ResponseEntity.ok(templateFolderResponse);
    }
}
