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

    public UserController(UserService service) {
        this.service = service;
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toSafe(user.get()));
    }

    /**
     * POST /api/users/login — 登录
     * 请求体: { "username": "happy", "password": "123456" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> user = service.login(request.username(), request.password());
        return user.map(u -> ResponseEntity.ok(toSafe(u)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "登录失败：用户名或密码不正确。")));
    }

    /** 返回给前端时不带密码哈希和盐 */
    private Map<String, Object> toSafe(User user) {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname()
        );
    }

    public record RegisterRequest(String username, String password, String nickname) {}
    public record LoginRequest(String username, String password) {}
}
