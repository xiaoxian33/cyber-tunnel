package com.cybertunnel.service;

import com.cybertunnel.model.LoginToken;
import com.cybertunnel.repository.LoginTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 登录凭证服务 —— 负责"发证"和"验证"
 *
 * 设计思路：
 *  - 登录/注册成功时调用 issue() 签发一张新凭证（7 天有效）
 *  - 后续每个请求带着 token 来，调用 resolve() 换回"这是哪个用户"
 *  - 这样后端就能确认身份，不再需要信任前端传来的 userId
 */
@Service
public class TokenService {

    /** 凭证有效期：7 天 */
    private static final Duration TOKEN_TTL = Duration.ofDays(7);

    private final LoginTokenRepository repository;

    public TokenService(LoginTokenRepository repository) {
        this.repository = repository;
    }

    /**
     * 为用户签发一张新凭证（同时作废该用户之前的旧凭证，防止一账号多端混乱）
     * @return 生成的 token 字符串，交给前端保存
     */
    @Transactional
    public String issue(Long userId) {
        // 同账号重新登录 -> 旧证全部作废，只留最新一张
        repository.deleteByUserId(userId);

        // UUID 随机生成一串几乎不可能重复的"身份证号"
        String tokenValue = UUID.randomUUID().toString().replace("-", "");
        LoginToken token = new LoginToken(tokenValue, userId, LocalDateTime.now().plus(TOKEN_TTL));
        repository.save(token);
        return tokenValue;
    }

    /**
     * 校验 token 并换回用户 id
     * @param tokenValue 前端 Header 里带来的凭证
     * @return 有效则返回该用户 id；无效/过期则返回空
     */
    @Transactional(readOnly = true)
    public Optional<Long> resolve(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return Optional.empty();
        }
        return repository.findByToken(tokenValue.trim())
                .filter(t -> t.getExpiresAt() == null || t.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(LoginToken::getUserId);
    }
}
