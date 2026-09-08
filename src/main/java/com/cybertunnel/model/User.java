package com.cybertunnel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户表 —— 社区里的每一位成员
 * 每个用户有自己独立的主页数据（备忘录/心情/用药/库存）
 */
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录用户名（全局唯一） */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 密码（存的是哈希值 + 盐，绝不明文存储） */
    @Column(nullable = false, length = 128)
    private String passwordHash;

    /** 加盐字符串，配合 passwordHash 使用 */
    @Column(nullable = false, length = 64)
    private String salt;

    /** 昵称（在公开区显示的名字） */
    @Column(nullable = false, length = 50)
    private String nickname;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public User() {}

    public User(String username, String passwordHash, String salt, String nickname) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.nickname = nickname;
    }

    // ====== Getter / Setter ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
