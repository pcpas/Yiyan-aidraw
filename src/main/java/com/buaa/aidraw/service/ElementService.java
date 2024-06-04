package com.buaa.aidraw.service;

import com.buaa.aidraw.mapper.ElementMapper;
import com.buaa.aidraw.model.domain.Element;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ElementService {

    @Resource
    ElementMapper elementMapper;
    @Resource
    ElasticSearchService elasticSearchService;

    public void addElement(String userId, String elementName, String prompt, boolean isPublic, String url, String pngPath) {
        Element element = new Element();
        element.setElementName(elementName);
        element.setPrompt(prompt);
        element.setUserId(userId);
        Date created_at = new Date();
        element.setCreateTime(created_at);
        element.setDelete(false);
        element.setPublic(isPublic);
        element.setElementUrl(pngPath);
        element.setFolderId("0");
        element.setFileUrl(url);

        elementMapper.insertElement(element);
        elasticSearchService.insertElement(element);
    }

    public List<Element> getElementsByUserId(String userId) {
        List<Element> elementList = elementMapper.getElementByUserId(userId);
        return elementList;
    }

    public List<Element> getTrashElements(String userId) {
        List<Element> elementList = elementMapper.getTrashElementByUserId(userId);
        return elementList;
    }

    public Element getElementById(String id){
        Element element = elementMapper.getElementById(id);
        return element;
    }

    public int updateFolder(String id, String folderId, boolean isDelete) {
        Element element = elementMapper.getElementById(id);
        if(!element.isDelete()){
            elasticSearchService.deleteElement(id);
        }
        element.setFolderId(folderId);
        element.setDelete(isDelete);
        if(!isDelete){
            elasticSearchService.insertElement(element);
        }
        return elementMapper.updateElement(element);
    }

    public int updatePublic(String id, boolean isPublic) {
        Element element = elementMapper.getElementById(id);
        element.setPublic(isPublic);
        elasticSearchService.deleteElement(id);
        elasticSearchService.insertElement(element);
        return elementMapper.updateElement(element);
    }

    public List<Element> getElementsByFolderId(String userId, String folderId) {
        List<Element> elementList = elementMapper.getElementByFolderId(userId, folderId);
        return elementList;
    }
}
