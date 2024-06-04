package com.buaa.aidraw.utils;

import com.buaa.aidraw.model.domain.Project;
import com.buaa.aidraw.model.domain.Template;

public class EsUtil {
    public static Template getEsTemplateEntity(Template t){
        Template project = new Template();
        project.setId(t.getId());
        project.setUserId(t.getUserId());
        project.setTemplateName(t.getTemplateName());
        project.setTemplateUrl(t.getTemplateUrl());
        project.setDelete(t.isDelete());
        project.setPublic(t.isPublic());
        project.setCreateTime(t.getCreateTime());
        project.setFolderId(t.getFolderId());
        return project;
    }

    public static Project getEsProjectEntity(Project p){
        Project project = new Project();
        project.setId(p.getId());
        project.setUserId(p.getUserId());
        project.setProjectName(p.getProjectName());
        project.setProjectUrl(p.getProjectUrl());
        project.setDelete(p.isDelete());
        project.setPublic(p.isPublic());
        project.setEditTime(p.getEditTime());
        project.setFolderId(p.getFolderId());
        return project;
    }
}
