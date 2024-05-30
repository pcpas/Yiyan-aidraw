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

    public int addFolder(String userId, int type) {
        Folder folder = new Folder();
        folder.setUserId(userId);
        folder.setType(type);
        folder.setFolderName("默认文件夹");
        folder.setDefault(false);

        return folderMapper.insert(folder);
    }

    public int updateFolder(String id, String name) {
        return folderMapper.update(id,name);
    }

    public Folder getFolder(String id) {
        return folderMapper.selectById(id);
    }

    public int deleteFolder(String id) {
        return folderMapper.delete(id);
    }
}
