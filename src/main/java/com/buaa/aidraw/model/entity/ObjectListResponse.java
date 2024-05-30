package com.buaa.aidraw.model.entity;

import com.buaa.aidraw.model.domain.Element;
import com.buaa.aidraw.model.domain.Project;
import com.buaa.aidraw.model.domain.Template;
import lombok.Data;

import java.util.List;

@Data
public class ObjectListResponse {
    private String type;
    private List<Element> elementList;
    private List<Project> projectList;
    private List<Template> templateList;
}
