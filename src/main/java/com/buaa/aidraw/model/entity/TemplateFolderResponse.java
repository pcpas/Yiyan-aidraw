package com.buaa.aidraw.model.entity;

import com.buaa.aidraw.model.domain.Template;
import lombok.Data;

import java.util.List;

@Data
public class TemplateFolderResponse {
    private List<ListResponse> data;
}
