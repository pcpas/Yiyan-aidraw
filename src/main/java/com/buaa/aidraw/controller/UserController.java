package com.buaa.aidraw.controller;

import co.elastic.clients.elasticsearch.watcher.Email;
import com.buaa.aidraw.exception.BaseException;
import com.buaa.aidraw.model.domain.User;
import com.buaa.aidraw.model.entity.StringResponse;
import com.buaa.aidraw.model.request.LoginRequest;
import com.buaa.aidraw.model.request.RegisterRequest;
import com.buaa.aidraw.model.request.StringRequest;
import com.buaa.aidraw.service.OpenAIService;
import com.buaa.aidraw.service.RedisService;
import com.buaa.aidraw.service.UserService;
import com.buaa.aidraw.utils.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

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
    public ResponseEntity<StringResponse> getSalt(@RequestBody StringRequest request) {
        String salt = userService.getSaltByEmail(request.getValue());
        return ResponseEntity.ok(new StringResponse(salt));
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

}
