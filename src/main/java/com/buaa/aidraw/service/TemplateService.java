package com.buaa.aidraw.service;

import com.aliyuncs.exceptions.ClientException;
import com.buaa.aidraw.config.OSSConfig;
import com.buaa.aidraw.mapper.TemplateMapper;
import com.buaa.aidraw.model.domain.Element;
import com.buaa.aidraw.model.domain.Project;
import com.buaa.aidraw.model.domain.Template;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TemplateService {
    @Resource
    TemplateMapper templateMapper;
    @Resource
    OSSConfig ossConfig;
    @Resource
    ElasticSearchService elasticSearchService;

    public String createTemplate(Project project) throws ClientException {
        Template template = new Template();
        template.setTemplateName(project.getProjectName());
        String templateUrl = ossConfig.copy("project","template", project.getProjectUrl());
        template.setTemplateUrl(templateUrl);
        template.setDelete(false);
        template.setFolderId("0");
        template.setPublic(false);
        Date created_at = new Date();
        template.setCreateTime(created_at);
        template.setFileUrl(project.getFileUrl());
        template.setUserId(project.getUserId());

        templateMapper.insertTemplate(template);
        elasticSearchService.insertTemplate(template);
        String id = template.getId();
        return id;
    }

    public List<Template> getTemplateBuUserId(String userId){
        List<Template> templateList = templateMapper.getTemplatesByUserId(userId);
        return  templateList;
    }

    public Template getTemplateById(String id){
        Template template = templateMapper.getTemplateById(id);
        return template;
    }

    public List<Template> getTrashTemplate(String userId){
        List<Template> templateList = templateMapper.getTrashTemplatesByUserId(userId);
        return templateList;
    }

    public int updateFolder(String id, String folderId, boolean isDelete) {
        Template template = templateMapper.getTemplateById(id);
        if(!template.isDelete()){
            elasticSearchService.deleteTemplate(id);
        }
        template.setFolderId(folderId);
        template.setDelete(isDelete);
        if(!isDelete){
            elasticSearchService.insertTemplate(template);
        }
        return templateMapper.updateTemplate(template);
    }

    public int updatePublic(String id, boolean isPublic) {
        Template template = templateMapper.getTemplateById(id);
        template.setPublic(isPublic);
        elasticSearchService.deleteTemplate(id);
        elasticSearchService.insertTemplate(template);
        return templateMapper.updateTemplate(template);
    }

    public List<Template> getTemplatesByFolderId(String userId, String folderId) {
        List<Template> templateList = templateMapper.getTemplateByFolderId(userId, folderId);
        return templateList;
    }
}
