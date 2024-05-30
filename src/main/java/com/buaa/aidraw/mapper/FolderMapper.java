package com.buaa.aidraw.mapper;

import com.buaa.aidraw.model.domain.Folder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FolderMapper {
    @Insert("INSERT INTO folder (userId, folderName, type, isDefault) VALUES (#{userId}, #{folderName}, #{type}, #{isDefault})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Folder folder);

    @Select("SELECT * FROM folder WHERE id = #{id}")
    Folder selectById(String id);

    @Select("SELECT * FROM folder WHERE (userId = #{userId} OR userId = 0) AND type=#{type} ORDER BY id DESC")
    List<Folder> selectByUserId(String userId, int type);

    @Update("UPDATE folder SET folderName = #{folderName} WHERE id = #{id}")
    int update(String id, String folderName);

    @Delete("DELETE FROM folder WHERE id = #{id}")
    int delete(String id);
}
