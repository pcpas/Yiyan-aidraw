package com.buaa.aidraw.mapper;

import com.buaa.aidraw.model.domain.Element;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ElementMapper {
    @Insert("INSERT INTO element (userId, elementName, elementUrl, prompt, folderId, isDelete, isPublic, createTime) VALUES (#{userId}, #{elementName}, #{elementUrl}, #{prompt}, #{folderId}, #{isDelete}, #{isPublic}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertElement(Element element);

    @Select("SELECT * FROM element WHERE id = #{id}")
    Element getElementById(String id);

    @Select("SELECT * FROM element WHERE userId = #{userId} AND isDelete = 0 ORDER BY id DESC")
    List<Element> getElementByUserId(String userId);

    @Update("UPDATE element SET elementName = #{elementName}, elementUrl = #{elementUrl}, folderId = #{folderId}, prompt = #{prompt}, isDelete = #{isDelete}, isPublic = #{isPublic} WHERE id = #{id}")
    void updateElement(Element element);

    @Update("UPDATE element SET isDelete = true WHERE id = #{id}")
    void softDeleteElement(String id);

    @Update("UPDATE element SET elementUrl = #{elementUrl} WHERE id = #{id}")
    void updateElementUrl(String elementUrl, String id);

    @Select("SELECT * FROM element WHERE userId = #{userId} AND isDelete = 1 ORDER BY id DESC")
    List<Element> getTrashElementByUserId(String userId);
}
