package com.buaa.aidraw.controller;

import com.buaa.aidraw.model.domain.Element;
import com.buaa.aidraw.model.domain.Folder;
import com.buaa.aidraw.model.domain.Project;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.entity.FileResponse;
import com.buaa.aidraw.model.entity.FolderListResponse;
import com.buaa.aidraw.model.entity.ProjectListResponse;
import com.buaa.aidraw.model.entity.StringResponse;
import com.buaa.aidraw.model.request.CreateRequest;
import com.buaa.aidraw.model.request.IdRequest;
import com.buaa.aidraw.model.request.StringRequest;
import com.buaa.aidraw.service.FolderService;
import com.buaa.aidraw.service.ProjectService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    public ResponseEntity<ProjectListResponse> getAllProjects(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();
        List<Project> projectList = projectService.getProjectsByUserId(userId);

        ProjectListResponse projectListResponse = new ProjectListResponse();
        projectListResponse.setProjectList(projectList);
        return ResponseEntity.ok(projectListResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createProject(@RequestBody CreateRequest createRequest, HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String projectName = createRequest.getName();
        boolean isPublic = createRequest.isPublic();
        projectService.addProject(userId, projectName, "", isPublic, "");

        return ResponseEntity.ok("成功");
    }

    @GetMapping("/trash")
    public ResponseEntity<List<Project>> trashElement(HttpServletRequest httpServletRequest) throws IOException {
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        List<Project> projectList = projectService.getTrashProjects(userId);
        return ResponseEntity.ok(projectList);
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

    @GetMapping("/data")
    public ResponseEntity<FileResponse> getProjectFile(@RequestBody IdRequest idRequest, HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String userId = user.getId();

        String id = String.valueOf(idRequest.getId());
        Project project = projectService.getProjectById(id);
        String file = project.getProjectUrl();

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
