package com.buaa.aidraw.service;

import com.buaa.aidraw.mapper.FolderMapper;
import com.buaa.aidraw.model.domain.Folder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderService {
    @Resource
    FolderMapper folderMapper;

    public List<Folder> getFolders(String userId, int type) {
        List<Folder> folderList = folderMapper.selectByUserId(userId, type);
        return folderList;
    }
}
