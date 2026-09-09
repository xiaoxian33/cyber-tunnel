package com.cybertunnel.service;

import com.cybertunnel.model.Memo;
import com.cybertunnel.repository.MemoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemoService {

    private final MemoRepository repository;

    public MemoService(MemoRepository repository) {
        this.repository = repository;
    }

    /** 获取用户所有备忘录 */
    @Transactional(readOnly = true)
    public List<Memo> findByUserId(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /** 新增备忘录 */
    @Transactional
    public Memo create(Long userId, String content) {
        Memo memo = new Memo(content);
        memo.setUserId(userId);
        return repository.save(memo);
    }

    /** 删除单条备忘录（归属校验：只允许删本人的） */
    @Transactional
    public void deleteById(Long userId, Long id) {
        if (repository.findByIdAndUserId(id, userId).isEmpty()) {
            return; // 不是本人的记录 -> 不删
        }
        repository.deleteById(id);
    }

    /** 清空用户所有备忘录 */
    @Transactional
    public void deleteAllByUserId(Long userId) {
        repository.deleteByUserId(userId);
    }
}
