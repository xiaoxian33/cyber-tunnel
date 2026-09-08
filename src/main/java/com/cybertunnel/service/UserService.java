package com.cybertunnel.service;

import com.cybertunnel.model.User;
import com.cybertunnel.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /** 获取所有用户（按注册顺序） */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAllByOrderByCreatedAtAsc();
    }

    /** 按 id 获取用户 */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * 注册新用户
     * @return 注册成功的用户；若用户名已存在返回 empty
     */
    @Transactional
    public Optional<User> register(String username, String rawPassword, String nickname) {
        String name = username == null ? "" : username.trim();
        String nick = nickname == null ? "" : nickname.trim();

        if (name.isEmpty() || rawPassword == null || rawPassword.isEmpty() || nick.isEmpty()) {
            return Optional.empty();
        }
        if (repository.existsByUsername(name)) {
            return Optional.empty(); // 用户名已存在
        }

        // 生成随机盐，密码加盐后哈希存储
        String salt = generateSalt();
        String hash = hashPassword(rawPassword, salt);

        User user = new User(name, hash, salt, nick);
        return Optional.of(repository.save(user));
    }

    /**
     * 登录校验
     * @return 校验通过返回用户对象，否则 empty
     */
    @Transactional(readOnly = true)
    public Optional<User> login(String username, String rawPassword) {
        Optional<User> found = repository.findByUsername(username == null ? "" : username.trim());
        if (found.isEmpty()) {
            return Optional.empty();
        }
        User user = found.get();
        String computed = hashPassword(rawPassword == null ? "" : rawPassword, user.getSalt());
        if (computed.equals(user.getPasswordHash())) {
            return found;
        }
        return Optional.empty();
    }

    // ====== 密码工具 ======

    /** 生成随机盐 */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    /** 密码加盐 SHA-256 哈希（转 16 进制字符串） */
    private String hashPassword(String rawPassword, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String salted = salt + rawPassword;
            byte[] hash = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
            // 再哈希一轮，提高破解成本
            return HexFormat.of().formatHex(digest.digest(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }
}
