package com.cybertunnel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 评论 —— 挂在某条用药记录下
 * 公开区：大家都能评论；私人区：自己评论回看
 */
@Entity
@Table(name = "post_comment")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属记录 id（medication_record.id） */
    @Column(name = "record_id", nullable = false)
    private Long recordId;

    /** 评论者用户ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 评论者昵称（冗余存储，避免每次查用户表，便于直接展示） */
    @Column(name = "user_nickname", length = 50)
    private String userNickname;

    /** 评论内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public PostComment() {}

    public PostComment(Long recordId, Long userId, String userNickname, String content) {
        this.recordId = recordId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.content = content;
    }

    // ====== Getter / Setter ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
