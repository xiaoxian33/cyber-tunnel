package com.cybertunnel.service;

import com.cybertunnel.model.PostComment;
import com.cybertunnel.model.PostLike;
import com.cybertunnel.repository.PostCommentRepository;
import com.cybertunnel.repository.PostLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SocialService {

    private final PostCommentRepository commentRepository;
    private final PostLikeRepository likeRepository;

    public SocialService(PostCommentRepository commentRepository, PostLikeRepository likeRepository) {
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    // ====== 评论 ======

    /** 获取某条记录的评论 */
    @Transactional(readOnly = true)
    public List<PostComment> comments(Long recordId) {
        return commentRepository.findByRecordIdOrderByCreatedAtAsc(recordId);
    }

    /** 新增评论 */
    @Transactional
    public Optional<PostComment> addComment(Long recordId, Long userId, String nickname, String content) {
        if (content == null || content.isBlank()) {
            return Optional.empty();
        }
        PostComment c = new PostComment(recordId, userId, nickname, content.trim());
        return Optional.of(commentRepository.save(c));
    }

    /** 删除评论 */
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    // ====== 点赞 ======

    /**
     * 切换点赞状态（每人只能点一次，再点取消）
     * 返回：{ liked: true/false, count: n }
     */
    @Transactional
    public Map<String, Object> toggleLike(Long recordId, Long userId) {
        Optional<PostLike> existing = likeRepository.findByRecordIdAndUserId(recordId, userId);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
        } else {
            likeRepository.save(new PostLike(recordId, userId));
        }
        long count = likeRepository.countByRecordId(recordId);
        boolean liked = existing.isEmpty();
        return Map.of("liked", liked, "count", count);
    }

    /** 获取某条记录的点赞数和"我是否已赞" */
    @Transactional(readOnly = true)
    public Map<String, Object> likeStatus(Long recordId, Long userId) {
        long count = likeRepository.countByRecordId(recordId);
        boolean liked = likeRepository.findByRecordIdAndUserId(recordId, userId).isPresent();
        return Map.of("liked", liked, "count", count);
    }
}
