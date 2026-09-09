# 🔐 登录态与权限安全改造记录（Token 认证）

> 从「前端自报家门」到「后端持证办事」的一次完整安全升级。
> 本文档同时是面试复习提纲：讲清楚**为什么要改、怎么改、解决了什么问题**。

---

## 一、改造前的问题：水平越权（IDOR）

### 现象

早期版本里，前后端靠一个"前端传 userId"来区分数据归属：

```javascript
// ❌ 前端(index.html) —— 自己填 userId
const CURRENT_USER_ID = JSON.parse(localStorage.getItem('currentUser')).id;
request('GET', '/memos?userId=' + CURRENT_USER_ID, ...);
```

```java
// ❌ 后端(Controller) —— 全盘相信前端
public ... listByUser(@RequestParam(defaultValue = "1") Long userId) {
    return service.findByUserId(userId);   // 你传 2 就查 2 号用户的数据
}
```

### 后果（面试要能讲出来）

- **水平越权 / IDOR**：把 `userId=1` 改成别人的 id，就能查看 / 修改 / 删除他人的私密记录。
- localStorage 是纯前端存储，**任何人都能改**。
- 更有默认值 `userId=1`，等于"没登录也默认操作 1 号用户"。
- 后端**没有任何手段验证**"你真的是这个人"。

> 💡 **安全第一原则**：**所有『我是谁』的信息，都不能由客户端声明，必须由服务端从凭证（token）推导。**

---

## 二、解决方案总览

```
登录成功 → 后端签发 token（一张只有后端能印的"身份证"）
每次请求 → 前端把 token 放在请求头：Authorization: Bearer <token>
后端拦截器 → 验 token → 解析出真实 userId → 放进"当前请求储物柜" CurrentUser
所有 Controller → 不再信前端传的 userId，只从 CurrentUser 拿
```

### 涉及的新文件

| 文件 | 职责 |
|---|---|
| `model/LoginToken.java` | 数据库表 `login_token`：存 token、user_id、过期时间 |
| `repository/LoginTokenRepository.java` | token 的查询 / 删除 |
| `service/TokenService.java` | **签发**（issue）和**校验**（resolve）凭证 |
| `config/AuthInterceptor.java` | 拦截器：每个 /api 请求先验 token，再放行 |
| `config/CurrentUser.java` | ThreadLocal 储物柜：保存"当前请求是哪个用户" |
| `config/WebConfig.java` | 注册拦截器 + 配置哪些路径放行 |

---

## 三、核心实现讲解

### 1. 签发凭证 —— `TokenService.issue()`

```java
@Transactional
public String issue(Long userId) {
    repository.deleteByUserId(userId);            // 同账号重新登录 → 旧证作废
    String tokenValue = UUID.randomUUID().toString().replace("-", "");
    LoginToken token = new LoginToken(tokenValue, userId, LocalDateTime.now().plus(TOKEN_TTL));
    repository.save(token);                        // 有效期 7 天
    return tokenValue;
}
```

要点：
- token 用 **UUID** 生成，几乎不可能被猜到/伪造。
- 设置了 **7 天有效期**，到期自动作废，需重新登录。
- 重新登录会作废旧 token（`deleteByUserId`），避免一账号多端混乱。

### 2. 校验凭证 —— `TokenService.resolve()`

```java
public Optional<Long> resolve(String tokenValue) {
    if (tokenValue == null || tokenValue.isBlank()) return Optional.empty();
    return repository.findByToken(tokenValue.trim())
            .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))  // 没过期
            .map(LoginToken::getUserId);                                  // 换成 userId
}
```

### 3. 拦截器安检 —— `AuthInterceptor.preHandle()`

```java
String auth = request.getHeader("Authorization");   // ① 取身份证
String token = auth.startsWith("Bearer ") ? auth.substring(7).trim() : null;

Optional<Long> userId = tokenService.resolve(token);  // ② 验证
if (userId.isEmpty()) {
    response.setStatus(401);                          // ③ 无效 → 挡在门外
    return false;
}
CurrentUser.set(userId.get());                        // ④ 有效 → 放进储物柜，放行
return true;
```

请求结束调用 `afterCompletion()` → `CurrentUser.clear()`，**防止线程复用导致串号**。

### 4. 当前用户储物柜 —— `CurrentUser`（ThreadLocal）

```java
private static final ThreadLocal<Long> HOLDER = new ThreadLocal<>();
```

为什么必须用 ThreadLocal？因为后端**同时服务很多用户**，每个请求跑在各自的线程里，ThreadLocal 保证"张三的请求只看到张三的 id，李四的请求只看到李四的"。

### 5. 放行路径 —— `WebConfig`

```java
registry.addInterceptor(authInterceptor)
        .addPathPatterns("/api/**")                       // 所有业务接口都过安检
        .excludePathPatterns("/api/users/register",        // 注册：还没有账号
                              "/api/users/login");         // 登录：就是为了换 token
```

---

## 四、Controller 层改造（核心：不再信前端 userId）

### 改造前后对比（以用药记录为例）

| 操作 | ❌ 改造前 | ✅ 改造后 |
|---|---|---|
| 查自己记录 | `?userId=1`（前端说了算） | `GET /medications` → `CurrentUser.id()` |
| 新增 | body 带 `userId` | 后端自动填 `CurrentUser.id()` |
| 改心得 | 知道 id 就能改 | `findByIdAndUserId(id, 我的id)` 归属校验 |
| 删除 | 知道 id 就能删 | 同上，仅限本人 |
| 归档/看板 | `?userId=x` | 自动取当前用户 |

### 归属校验的标准写法（Repository）

```java
// Repository：按 "id + 我的身份" 双条件查询
Optional<MedicationRecord> findByIdAndUserId(Long id, Long userId);
```

```java
// Service：查不到 / 不是我的 → 拒绝操作
@Transactional
public void deleteById(Long userId, Long id) {
    if (repository.findByIdAndUserId(id, userId).isEmpty()) return;
    repository.deleteById(id);
}
```

### 额外修掉的漏洞

1. **评论冒充昵称**：昵称不再由前端传，后端根据 userId 从用户表查出真实昵称。
2. **库存数据模型缺陷**：`medicine_name` 全局唯一 → 改为 `(user_id, medicine_name)` 联合唯一，每个用户可拥有同名药且互不干扰。
3. 所有 Controller 的 DTO 里**删掉了 userId 字段**，前端想传也没地方用了。

---

## 五、改造范围

| 文件类别 | 改动 |
|---|---|
| 新增 | `LoginToken`、`LoginTokenRepository`、`TokenService`、`AuthInterceptor`、`CurrentUser`、`WebConfig` |
| Controller | `User`（登录注册签发token）、`MedicationRecord`、`Memo`、`MedPlan`、`Medicine`、`MedicineInventory`、`Social` |
| Service/Repository | 相应增加 `findByIdAndUserId` 等归属校验方法 |

> ⚠️ 说明：改造涉及前后端两段。**后端先完成，前端尚未改造成携带 token 的版本前，页面会 401**——这是分阶段迁移的正常中间状态。

---

## 六、面试常见追问与回答提纲

**Q1：为什么要自己写 token，不用 Spring Security / JWT？**
> 为了让认证流程更透明、可解释，先用轻量方案（数据库存 token）把**认证的核心概念**跑通：签发、校验、有效期、拦截器、上下文传递。后续可以平滑升级为 Spring Security + JWT，业务代码不用大改。

**Q2：token 存在数据库里，重启会怎样？**
> 表是持久化的，token 重启后依然有效，直到 7 天过期。

**Q3：怎么防 token 被盗用？**
> 目前是单 token 方案；正式上生产可加：HTTPS 传输、token 定期轮换、登录设备管理、失败尝试限流。README 免责声明已注明本项目为学习用途。

**Q4：ThreadLocal 会内存泄漏吗？**
> 请求结束后在 `afterCompletion` 里 `clear()`，就是为了防止线程池复用线程时残留上一个用户的数据。

**Q5：为什么昵称要后端查表，不能前端传？**
> 因为昵称代表"我是谁"，属于身份信息。身份信息若由客户端声明，任何人都能冒充他人发言。

---

*Made with 💖 · Cyber-Tunnel · 登录态与权限安全改造记录*
