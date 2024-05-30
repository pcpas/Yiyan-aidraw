package com.buaa.aidraw.mapper;

import com.buaa.aidraw.model.domain.Project;
import com.buaa.aidraw.model.domain.Template;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TemplateMapper {
    @Insert("INSERT INTO template (userId, templateName, templateUrl, fileUrl, isDelete, isPublic, createTime, folderId) " +
            "VALUES (#{userId}, #{templateName}, #{templateUrl}, #{fileUrl}, #{isDelete}, #{isPublic}, #{createTime}, #{folderId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertTemplate(Template template);

    @Select("SELECT * FROM template WHERE id = #{id}")
    Template getTemplateById(String id);

    @Select("SELECT * FROM template WHERE userId = #{userId} AND isDelete = 0 ORDER BY createTime DESC")
    List<Template> getTemplatesByUserId(String userId);

    @Select("SELECT * FROM template WHERE userId = #{userId} AND isDelete = 1 ORDER BY createTime DESC")
    List<Template> getTrashTemplatesByUserId(String userId);

    @Update("UPDATE template SET templateName = #{templateName}, templateUrl = #{templateUrl}, fileUrl = #{fileUrl}, " +
            "isDelete = #{isDelete}, isPublic = #{isPublic}, createTime = #{createTime}, folderId = #{folderId} WHERE id = #{id}")
    int updateTemplate(Template template);

    @Delete("DELETE FROM template WHERE id = #{id}")
    void deleteTemplate(String id);

    @Select("SELECT * FROM template WHERE userId = #{userId} AND folderId = #{folderId} AND isDelete = 0 ORDER BY id DESC")
    List<Template> getTemplateByFolderId(String userId, String folderId);
}
