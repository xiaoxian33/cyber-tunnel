package com.cybertunnel.repository;

import com.cybertunnel.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    /** 获取某条记录的所有评论（时间顺序排列） */
    List<PostComment> findByRecordIdOrderByCreatedAtAsc(Long recordId);

    /** 删除某记录的全部评论（删除记录时级联清理） */
    void deleteByRecordId(Long recordId);
}
