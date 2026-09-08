package com.cybertunnel.controller;

import com.cybertunnel.model.PostComment;
import com.cybertunnel.service.SocialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/social")
public class SocialController {

    private final SocialService service;

    public SocialController(SocialService service) {
        this.service = service;
    }

    // ====== 评论 ======

    /** GET /api/social/comments?recordId=1 — 获取某条记录的评论 */
    @GetMapping("/comments")
    public ResponseEntity<List<PostComment>> comments(@RequestParam Long recordId) {
        return ResponseEntity.ok(service.comments(recordId));
    }

    /**
     * POST /api/social/comments — 新增评论
     * 请求体: { "recordId":1, "userId":1, "nickname":"阿莉丝", "content":"今天感觉不错" }
     */
    @PostMapping("/comments")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest request) {
        if (request.recordId() == null || request.content() == null || request.content().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return service.addComment(request.recordId(), request.userId(), request.nickname(), request.content())
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c))
                .orElse(ResponseEntity.badRequest().build());
    }

    /** DELETE /api/social/comments/{id} — 删除评论 */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        service.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    public record CommentRequest(Long recordId, Long userId, String nickname, String content) {}

    // ====== 点赞 ======

    /**
     * POST /api/social/like — 切换点赞
     * 请求体: { "recordId":1, "userId":1 }
     * 返回 { liked, count }
     */
    @PostMapping("/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody LikeRequest request) {
        return ResponseEntity.ok(service.toggleLike(request.recordId(), request.userId()));
    }

    /**
     * GET /api/social/like/status?recordId=1&userId=1 — 查询点赞状态
     * 返回 { liked, count }
     */
    @GetMapping("/like/status")
    public ResponseEntity<Map<String, Object>> likeStatus(@RequestParam Long recordId, @RequestParam(defaultValue = "1") Long userId) {
        return ResponseEntity.ok(service.likeStatus(recordId, userId));
    }

    public record LikeRequest(Long recordId, Long userId) {}
}
