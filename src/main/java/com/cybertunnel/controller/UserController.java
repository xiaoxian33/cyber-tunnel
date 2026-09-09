package com.cybertunnel.controller;

import com.cybertunnel.model.User;
import com.cybertunnel.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final com.cybertunnel.service.TokenService tokenService;

    public UserController(UserService service, com.cybertunnel.service.TokenService tokenService) {
        this.service = service;
        this.tokenService = tokenService;
    }

    /** GET /api/users — 获取所有用户（公开会员列表用） */
    @GetMapping
    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    /** GET /api/users/{id} — 查询单用户 */
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/users/register — 注册
     * 请求体: { "username": "happy", "password": "123456", "nickname": "小糖" }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Optional<User> user = service.register(request.username(), request.password(), request.nickname());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "注册失败：用户名可能已存在，或信息填写不完整。"));
        }
        User saved = user.get();
        String token = tokenService.issue(saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toSafe(saved, token));
    }

    /**
     * POST /api/users/login — 登录（成功后签发登录凭证 token）
     * 请求体: { "username": "happy", "password": "123456" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> user = service.login(request.username(), request.password());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "登录失败：用户名或密码不正确。"));
        }
        User u = user.get();
        String token = tokenService.issue(u.getId());
        return ResponseEntity.ok(toSafe(u, token));
    }

    /** 返回给前端时不带密码哈希和盐，同时带上登录凭证 token */
    private Map<String, Object> toSafe(User user, String token) {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname(),
                "token", token
        );
    }

    public record RegisterRequest(String username, String password, String nickname) {}
    public record LoginRequest(String username, String password) {}
}
