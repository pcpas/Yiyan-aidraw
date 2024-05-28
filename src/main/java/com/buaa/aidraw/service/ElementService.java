package com.buaa.aidraw.service;

import com.buaa.aidraw.mapper.ElementMapper;
import com.buaa.aidraw.model.domain.Element;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ElementService {

    @Resource
    ElementMapper elementMapper;

    public void addElement(String userId, String elementName, String prompt, boolean isPublic, String url) {
        Element element = new Element();
        element.setElementName(elementName);
        element.setPrompt(prompt);
        element.setUserId(userId);
        Date created_at = new Date();
        element.setCreateTime(created_at);
        element.setDelete(false);
        element.setPublic(isPublic);
        element.setElementUrl(url);

        elementMapper.insertElement(element);
    }
}
