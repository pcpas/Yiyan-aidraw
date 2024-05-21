package com.buaa.aidraw.service;

import com.buaa.aidraw.exception.BaseException;
import com.buaa.aidraw.mapper.UserMapper;
import com.buaa.aidraw.model.domain.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;

@Service
public class UserService {


    @Resource
    UserMapper userMapper;

    public static String saltEncryption(String password, String salt) {
        password = password + salt;
        return DigestUtils.sha256Hex(password);
    }


    public void register(String username, String password, String email, String salt) {

        boolean isEmailValid = email.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        if (!isEmailValid) {
            throw new BaseException("邮箱格式不正确");
        }
        User user = userMapper.getUserByEmail(email);
        Date created_at = new Date();
        if (user != null) {
            throw new BaseException("邮箱已注册");
        }
        user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setSalt(salt);
        user.setPassword(saltEncryption(password, salt));
        user.setRegisterTime(created_at);
        userMapper.insertUser(user);
    }

    public User login(String email, String password) {
        User user = userMapper.getUserByEmail(email);
        if (user == null) {
            throw new BaseException("邮箱未注册");
        }
        password = saltEncryption(password, user.getSalt());
        if (!user.getPassword().equals(password)) {
            throw new BaseException("密码不正确");
        }
        return user;
    }

    public User getUserInfo(String id){
        User user = userMapper.getUserById(id);
        if(user==null)
            throw new BaseException("用户不存在");
        return user;
    }


}