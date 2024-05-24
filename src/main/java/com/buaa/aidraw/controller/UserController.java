package com.buaa.aidraw.controller;

import com.buaa.aidraw.exception.BaseException;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.request.LoginRequest;
import com.buaa.aidraw.model.request.RegisterRequest;
import com.buaa.aidraw.service.RedisService;
import com.buaa.aidraw.service.UserService;
import com.buaa.aidraw.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;
    @Resource
    RedisService redisService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest httpServletRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();
        String salt = registerRequest.getSalt();
        userService.register(username, password, email, salt);
        return ResponseEntity.ok("注册成功");
    }

    @GetMapping("/getSalt")
    public ResponseEntity<String> getSalt(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");
        return ResponseEntity.ok(user.getSalt());
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        User user = userService.login(email, password);
        Map<String, Object> map = user.toDict();
        String token = JwtUtil.generateToken(user.getId());
        map.put("token", token);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(HttpServletRequest httpServletRequest) {
        User user = (User) httpServletRequest.getAttribute("user");

        if (user == null)
            throw new BaseException("用户不存在");
        return ResponseEntity.ok(user.toDict());
    }

    @GetMapping("/test")
    public ResponseEntity<User> test(HttpServletRequest httpServletRequest){
        User user = (User) httpServletRequest.getAttribute("user");
        String id = user.getId();
        User res = redisService.getObject(id, User.class);
        if(res == null){
            res = userService.getUserInfo(id);
            System.out.println("No Redis");
            redisService.add(id, res);
        }
        return ResponseEntity.ok(res);
    }
}
