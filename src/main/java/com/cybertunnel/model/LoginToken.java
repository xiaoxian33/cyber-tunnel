package com.cybertunnel.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 登录凭证(token)表 —— 登录成功后后端签发的一张"身份证"
 * 前端后续请求带上 token，后端凭它确认"你是谁"，不再信任前端传来的 userId
 */
@Entity
@Table(name = "login_token")
public class LoginToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** token 值（随机生成，全局唯一） */
    @Column(nullable = false, unique = true, length = 64)
    private String token;

    /** 这张"身份证"属于哪个用户 */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 签发时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 过期时间（到期后这张证作废，需重新登录） */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public LoginToken() {}

    public LoginToken(String token, Long userId, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    // ====== Getter / Setter ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
