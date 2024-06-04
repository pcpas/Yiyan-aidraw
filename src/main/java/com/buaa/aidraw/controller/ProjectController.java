package com.buaa.aidraw.controller;

import com.buaa.aidraw.config.OSSConfig;
import com.buaa.aidraw.model.domain.*;
import com.buaa.aidraw.model.entity.*;
import com.buaa.aidraw.model.request.CreateRequest;
import com.buaa.aidraw.model.request.IdRequest;
import com.buaa.aidraw.model.request.StringRequest;
import com.buaa.aidraw.model.request.ValueRequest;
import com.buaa.aidraw.service.ElasticSearchService;
import com.buaa.aidraw.service.FolderService;
import com.buaa.aidraw.service.ProjectService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {
    @Resource
    ProjectService projectService;
    @Resource
    FolderService folderService;
    @Resource
    OSSConfig ossConfig;
    @Resource
    ElasticSearchService elasticSearchService;

    @PostMapping("/img")
    public ResponseEntity<ImgUrlResponse> uploadElement(@RequestPart("img") MultipartFile image,
                                                             @RequestPart("id") String id,
                                                             HttpServletRequest httpServletRequest) throws IOException {
        if(ObjectUtils.isEmpty(image) || image.getSize() <= 0){
            return ResponseEntity.badRequest().body(new ImgUrlResponse(null));
        }
        String name = id + ".png";
        String res = ossConfig.upload(image, "project", name);
        if (res != null){
            return ResponseEntity.ok(new ImgUrlResponse(res));
        } else {
            return ResponseEntity.badRequest().body(new ImgUrlResponse(null));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ProjectListResponse> getAllProjects(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();
        List<Project> projectList = projectService.getProjectsByUserId(userId);

        ProjectListResponse projectListResponse = new ProjectListResponse();
        projectListResponse.setProjectList(projectList);
        return ResponseEntity.ok(projectListResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<IdResponse> createProject(@RequestBody CreateRequest createRequest, HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String projectName = createRequest.getName();
        boolean isPublic = createRequest.isPublic();
        String id = projectService.addProject(userId, projectName, "", isPublic, "");

        IdResponse idResponse = new IdResponse();
        idResponse.setId(id);
        return ResponseEntity.ok(idResponse);
    }

    @GetMapping("/trash")
    public ResponseEntity<ObjectListResponse> trashElement(HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Project> projectList = projectService.getTrashProjects(userId);
        ObjectListResponse objectListResponse = new ObjectListResponse();
        objectListResponse.setProjectList(projectList);
        return ResponseEntity.ok(objectListResponse);
    }

    @GetMapping("/folder")
    public ResponseEntity<FolderListResponse> folder(HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Folder> folderList = folderService.getFolders(userId, 2);
        FolderListResponse folderListResponse = new FolderListResponse();
        folderListResponse.setFolderList(folderList);
        return ResponseEntity.ok(folderListResponse);
    }

    @PostMapping("/data")
    public ResponseEntity<FileResponse> getProjectFile(@RequestBody IdRequest idRequest, HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String id = String.valueOf(idRequest.getId());
        Project project = projectService.getProjectById(id);
        String file = project.getFileUrl();

        FileResponse fileResponse = new FileResponse();
        fileResponse.setFile(file);

        return ResponseEntity.ok(fileResponse);
    }

    @PostMapping("/modify")
    public ResponseEntity<StringResponse> modifyProject(@RequestBody Project project, HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        project.setUserId(userId);
        Date created_at = new Date();
        project.setEditTime(created_at);

        int res = projectService.updateProject(project);
        if(res == 0){
            return ResponseEntity.ok(new StringResponse("成功"));
        } else {
            return ResponseEntity.badRequest().body(new StringResponse("失败"));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<ProjectListResponse> searchTemplate(@RequestBody ValueRequest valueRequest, HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String value = valueRequest.getValue();
        int pageNo = valueRequest.getPageNo();
        List<Project> list = elasticSearchService.searchProject(value, pageNo, 20);
        ProjectListResponse projectListResponse = new ProjectListResponse();
        projectListResponse.setProjectList(list);
        return ResponseEntity.ok(projectListResponse);
    }


//    @PostMapping("/img")
//    public ResponseEntity<StringResponse> upload(@RequestPart MultipartFile img,
//                                                 @RequestPart Integer id,
//                                                 HttpServletRequest httpServletRequest){
//        User user = (User) httpServletRequest.getAttribute("user");
//        String userId = user.getId();
//
//        String type = (String) httpServletRequest.getAttribute("Content-Type");
//
//    }
}
