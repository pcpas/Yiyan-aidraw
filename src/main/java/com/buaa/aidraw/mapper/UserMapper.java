package com.buaa.aidraw.mapper;


import com.buaa.aidraw.model.domain.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO User (username, email, password, register_time,salt) VALUES (#{username}, #{email}, #{password}, #{registerTime}, #{salt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertUser(User user);

    @Select("SELECT * FROM User WHERE email=#{email} ORDER BY id DESC LIMIT 1")
    User getUserByEmail(String email);

    @Select("SELECT * FROM User WHERE id=#{id} ORDER BY id DESC LIMIT 1 ")
    User getUserById(String id);

    @Update("UPDATE User SET isActive = true WHERE id = #{id}")
    void updateUser(User user);


    @Update("UPDATE User SET avatarUrl=#{avatarUrl, jdbcType=VARCHAR}  WHERE id = #{id}")
    void updateAvatarUrl(String avatarUrl,int id);


}
