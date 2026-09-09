package com.cybertunnel.service;

import com.cybertunnel.model.PostComment;
import com.cybertunnel.model.PostLike;
import com.cybertunnel.model.User;
import com.cybertunnel.repository.PostCommentRepository;
import com.cybertunnel.repository.PostLikeRepository;
import com.cybertunnel.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SocialService {

    private final PostCommentRepository commentRepository;
    private final PostLikeRepository likeRepository;
    private final UserRepository userRepository;

    public SocialService(PostCommentRepository commentRepository,
                         PostLikeRepository likeRepository,
                         UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    // ====== 评论 ======

    /** 获取某条记录的评论 */
    @Transactional(readOnly = true)
    public List<PostComment> comments(Long recordId) {
        return commentRepository.findByRecordIdOrderByCreatedAtAsc(recordId);
    }

    /**
     * 新增评论
     * 昵称不信任前端传入 —— 后端根据 userId 从用户表查出真实昵称，防止冒充他人
     */
    @Transactional
    public Optional<PostComment> addComment(Long recordId, Long userId, String content) {
        if (content == null || content.isBlank()) {
            return Optional.empty();
        }
        // 从用户表取真实昵称（后端自己确认，不信前端）
        Optional<User> user = userRepository.findById(userId);
        String nickname = user.map(User::getNickname).orElse("社区成员");
        PostComment c = new PostComment(recordId, userId, nickname, content.trim());
        return Optional.of(commentRepository.save(c));
    }

    /**
     * 删除评论（归属校验：只允许删除自己的评论）
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        commentRepository.findByIdAndUserId(commentId, userId)
                .ifPresent(commentRepository::delete);
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
