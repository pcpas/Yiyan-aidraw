package com.buaa.aidraw.mapper;

import com.buaa.aidraw.model.domain.Element;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ElementMapper {
    @Insert("INSERT INTO element (userId, elementName, elementUrl, prompt, isDelete, isPublic, createTime) VALUES (#{userId}, #{elementName}, #{elementUrl}, #{prompt}, #{isDelete}, #{isPublic}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertElement(Element element);

    @Select("SELECT * FROM element WHERE id = #{id}")
    Element getElementById(String id);

    @Select("SELECT * FROM element WHERE userId = #{userId} ORDER BY id DESC LIMIT 1")
    Element getElementByUserId(String userId);

    @Update("UPDATE element SET elementName = #{elementName}, elementUrl = #{elementUrl}, folderId = #{folderId}, prompt = #{prompt}, isDelete = #{isDelete}, isPublic = #{isPublic} WHERE id = #{id}")
    void updateElement(Element element);

    @Update("UPDATE element SET isDelete = true WHERE id = #{id}")
    void softDeleteElement(String id);

    @Update("UPDATE element SET elementUrl = #{elementUrl} WHERE id = #{id}")
    void updateElementUrl(String elementUrl, String id);
}
