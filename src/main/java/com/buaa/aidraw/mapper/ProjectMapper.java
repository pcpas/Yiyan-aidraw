package com.buaa.aidraw.mapper;

import com.buaa.aidraw.model.domain.Element;
import com.buaa.aidraw.model.domain.Project;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProjectMapper {
    @Select("SELECT * FROM project WHERE id = #{id}")
    Project getProjectById(String id);

    @Select("SELECT id, userId, projectName, projectUrl, isDelete, isPublic, editTime, folderId FROM project WHERE userId = #{userId} AND isDelete = 0 ORDER BY id DESC")
    List<Project> getProjectByUserId(String userId);

    @Insert("INSERT INTO project (userId, projectName, projectUrl, isDelete, isPublic, fileUrl, editTime, folderId) VALUES (#{userId}, #{projectName}, #{projectUrl}, #{isDelete}, #{isPublic}, #{fileUrl}, #{editTime}, #{folderId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertProject(Project project);

    @Update("UPDATE project SET userId = #{userId}, projectName = #{projectName}, projectUrl = #{projectUrl}, isDelete = #{isDelete}, isPublic = #{isPublic}, fileUrl = #{fileUrl}, editTime = #{editTime}, folderId = #{folderId} WHERE id = #{id}")
    int updateProject(Project project);

    @Delete("DELETE FROM project WHERE id = #{id}")
    void deleteProject(String id);

    @Select("SELECT * FROM project WHERE userId = #{userId} AND isDelete = 1 ORDER BY id DESC")
    List<Project> getTrashProjectByUserId(String userId);

    @Select("SELECT * FROM project WHERE userId = #{userId} AND folderId = #{folderId} AND isDelete = 0 ORDER BY id DESC")
    List<Project> getProjectByFolderId(String userId, String folderId);
}
