package com.buaa.aidraw.model.entity;

import com.buaa.aidraw.model.domain.Project;
import lombok.Data;

import java.util.List;

@Data
public class ProjectListResponse {
    private List<Project> projectList;
}
