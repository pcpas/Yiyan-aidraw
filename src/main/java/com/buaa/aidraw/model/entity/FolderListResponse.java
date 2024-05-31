package com.buaa.aidraw.model.entity;

import com.buaa.aidraw.model.domain.Folder;
import lombok.Data;

import java.util.List;

@Data
public class FolderListResponse {
    private List<Folder> folderList;
}
