package com.buaa.aidraw.service;

import com.buaa.aidraw.mapper.TemplateMapper;
import com.buaa.aidraw.model.domain.Element;
import com.buaa.aidraw.model.domain.Template;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {
    @Resource
    TemplateMapper templateMapper;
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
        template.setFolderId(folderId);
        template.setDelete(isDelete);
        return templateMapper.updateTemplate(template);
    }

    public int updatePublic(String id, boolean isPublic) {
        Template template = templateMapper.getTemplateById(id);
        template.setPublic(isPublic);
        return templateMapper.updateTemplate(template);
    }

    public List<Template> getTemplatesByFolderId(String userId, String folderId) {
        List<Template> templateList = templateMapper.getTemplateByFolderId(userId, folderId);
        return templateList;
    }
}
