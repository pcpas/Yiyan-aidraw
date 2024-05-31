package com.buaa.aidraw.service;

import com.buaa.aidraw.mapper.ElementMapper;
import com.buaa.aidraw.mapper.ProjectMapper;
import com.buaa.aidraw.model.domain.Element;
import com.buaa.aidraw.model.domain.Project;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProjectService {

    @Resource
    ProjectMapper projectMapper;

    public String addProject(String userId, String projectName, String projectUrl, boolean isPublic, String url) {
        Project project = new Project();
        project.setUserId(userId);
        project.setProjectName(projectName);
        project.setProjectUrl(projectUrl);
        project.setDelete(false);
        project.setPublic(isPublic);
        project.setFileUrl(url);
        Date created_at = new Date();
        project.setEditTime(created_at);
        project.setFolderId("0");

        projectMapper.insertProject(project);
        return project.getId();
    }

    public List<Project> getProjectsByUserId(String userId) {
        List<Project> projectList = projectMapper.getProjectByUserId(userId);
        return projectList;

    }

    public List<Project> getTrashProjects(String userId) {
        List<Project> projectList = projectMapper.getTrashProjectByUserId(userId);
        return projectList;

    }

    public Project getProjectById(String id){
        Project project = projectMapper.getProjectById(id);
        return project;
    }

    public int updateProject(Project project){
        projectMapper.updateProject(project);
        return 0;
    }

    public int updateFolder(String id, String folderId, boolean isDelete) {
        Project project = projectMapper.getProjectById(id);
        project.setFolderId(folderId);
        project.setDelete(isDelete);
        return projectMapper.updateProject(project);
    }

    public List<Project> getProjectsByFolderId(String userId, String folderId) {
        List<Project> projectList = projectMapper.getProjectByFolderId(userId, folderId);
        return projectList;
    }


}
