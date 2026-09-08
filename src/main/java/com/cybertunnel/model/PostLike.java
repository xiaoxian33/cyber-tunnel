package com.cybertunnel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 点赞 —— 公开记录上的赞
 * 通过 (recordId, userId) 唯一约束保证每个用户只能对同一条记录点一次赞
 */
@Entity
@Table(name = "post_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"record_id", "user_id"}))
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 被赞的记录 id */
    @Column(name = "record_id", nullable = false)
    private Long recordId;

    /** 点赞用户ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public PostLike() {}

    public PostLike(Long recordId, Long userId) {
        this.recordId = recordId;
        this.userId = userId;
    }

    // ====== Getter / Setter ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
