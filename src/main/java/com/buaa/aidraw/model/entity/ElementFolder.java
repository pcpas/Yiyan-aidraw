package com.buaa.aidraw.model.entity;

import com.buaa.aidraw.model.domain.Element;
import lombok.Data;

import java.util.List;

@Data
public class ElementFolder {
    private String id;
    private String folderName;
    private List<Element> list;
}
