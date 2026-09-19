# 💊 Cyber-Tunnel · 用药记录与心得分享社区

面向**需要长期、规律用药的人群（尤其青少年）**的自我管理与同伴支持工具：
把「用药记账 + 心得手账 + 支持型社区」合在一起 —— 私密地管好自己的真实用药数据，也可以在愿意的时候分享心路、互相照看。

> 出发点：很多人不缺少药，缺少的是被理解的安全感。

## 在线演示（临时）

🔗 **https://candied-dutiful-schilling.ngrok-free.dev** —— 账号 `tester01` / `pass1234`

> ⚠️ 由 ngrok 免费隧道提供，**只在演示时开启**；本机关机或隧道关闭即失效。正式部署后可提供长期地址。

## 截图

![登录页 · 复古像素风](docs/images/login.png)

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Java 17 · Spring Boot · Spring Data JPA (Hibernate) |
| 数据库 | MySQL 8 · 10 张表 |
| 登录态 | 后端 Token 认证：登录签发令牌 → 拦截器统一鉴权 → 数据按当前用户隔离 |
| 前端 | 原生 HTML / CSS / JavaScript（无框架）· 复古像素风 |

## 设计上的几个取舍（为什么这么做）

| 取舍 | 做法 | 为什么 |
|---|---|---|
| **修掉水平越权（IDOR）** | 从"前端传 userId"改成"后端从令牌推出当前用户"，并在查询层统一做归属校验 | 前端传什么都能被伪造，身份必须由后端说了算（改造记录与面试提纲见 `docs/AUTH-SECURITY.md`） |
| **登录态放数据库** | 令牌存 `login_token` 表（含过期时间），拦截器每次校验有效性 | 后端重启不掉线；以后要多实例部署也不用改逻辑 |
| **当前用户用 ThreadLocal 传** | `AuthInterceptor` 校验通过后放进 `CurrentUser`，Service 直接取 | 不用每个方法都带一个 userId 参数，也避免漏传 |
| **业务规则放 Service** | 8 个 Service 承担规则；Controller 只做"收请求 → 调服务 → 还数据" | 风险判定、统计、库存、方案登记这些规则不属于接口层 |
| **可见性由数据决定** | 每条记录带公开 / 私密标记，公开流只查公开的 | 隐私默认安全：不主动公开，别人就看不到 |
| **方案模板是一对多** | `med_plan` + `med_plan_item`（一条方案多行明细） | 一次登记一套常用组合，避免重复录入 |
| **风险统计在后端算** | `MedicationRecordService.riskStats()`：近 7/14/30 天风险次数、本周 vs 上周、连续记录天数 | 前端只负责画图，口径统一在后端 |

## 功能

- **用药记录**：药品、片数、时间、公开 / 私密；每条都能写下一段心得
- **剂量风险预警 + 看板**：超量标记 ⚠️；近 7/14/30 天次数、周对比、连续记录天数
- **用药方案**：把常用组合存成模板，一键批量登记
- **药品库存**：记录每种药的剩余片数，避免断药
- **社区**：私人 / 公开两栏；点赞与评论
- **备忘录**：随手记，按时间倒序
- **用户系统**：注册 / 登录 / 退出，数据按用户隔离

## 接口（分组摘要）

| 分组 | 主要路径 |
|---|---|
| 用户 | `POST /api/users/register` · `POST /api/users/login` · `GET /api/users` |
| 用药记录 | `GET/POST/PATCH/DELETE /api/medications` · `GET /api/medications/public` · `GET /api/medications/stats/risk` |
| 方案 / 备忘 / 库存 | `/api/plans` · `/api/memos` · `/api/inventory` |
| 互动 | `POST /api/social/like` · `GET /api/social/like/status` · `GET/POST/DELETE /api/social/comments` |

除注册与登录外，其余接口都需要请求头 `Authorization: Bearer <令牌>`，且只能访问属于自己的数据。

## 本地运行

前置：JDK 17+、MySQL 8（项目自带 `mvnw` 包装器）。

```sql
-- 1) 建库（JPA 会按实体自动建表）
CREATE DATABASE cyber_tunnel_db;
```

```bash
# 2) 配置连接：推荐用环境变量，避免把密码写进仓库
$env:DB_USERNAME="root"; $env:DB_PASSWORD="你的密码"   # Windows PowerShell
export DB_USERNAME=root DB_PASSWORD=你的密码           # macOS / Linux

# 3) 启动
./mvnw spring-boot:run      # Windows: mvnw spring-boot:run
```

打开 <http://localhost:8080>（未登录会自动跳转到 `login.html`），注册一个账号即可进入。

## 目录结构

    src/main/java/com/cybertunnel/
      controller/   7 个 REST 控制器（只负责收请求、调服务）
      service/      8 个 Service（风险判定、统计、库存、方案登记…）
      model/        10 个 JPA 实体（= 10 张表）
      repository/   Spring Data 仓库
      config/       AuthInterceptor / CurrentUser / Jackson / WebConfig
    src/main/resources/static/    前端页面（index.html · login.html）与像素素材
    docs/AUTH-SECURITY.md         登录态与权限安全改造记录（含面试问答提纲）

## 免责声明

本项目是个人学习 / 自我管理 / 同伴支持工具，**不构成任何医疗建议**。
用药请遵医嘱；如遇疑似过量或紧急情况，请立即联系专业医疗机构或拨打急救电话。

---

尚未指定开源协议；有意合作或开源，欢迎联系作者。
