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

    @Select("SELECT * FROM folder WHERE userId = #{userId} AND type=#{type} ORDER BY id DESC")
    List<Folder> selectByUserId(String userId, int type);

    @Update("UPDATE folder SET userId = #{userId}, folderName = #{folderName}, type = #{type}, isDefault = #{isDefault} WHERE id = #{id}")
    int update(Folder folder);

    @Delete("DELETE FROM folder WHERE id = #{id}")
    int delete(String id);
}
