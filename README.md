# 💊 Cyber-Tunnel · 用药记录与心得分享社区

> 🕹️ 复古像素赛博风 · 「诚实记录用药，温柔分享心得」

欢迎光临。这里不是普通的药箱，而是一条通往自我健康管理的 **赛博隧道** 🚇。

Cyber-Tunnel 是一个面向 **需要长期、规律用药的人群（尤其是青少年）** 的自我管理与同伴支持社区。它把「**用药记账本 + 心得手账 + 支持型社区**」合为一体：既私密地管理自己的真实用药数据，也能在彼此尊重的前提下分享心路、互相照看。

我们相信：**诚实记录每一次用药，是对自己最好的照顾；把这份诚实分享出去，能温暖更多人。** 🌍

---

## ✨ 核心亮点

- 🎨 **复古像素赛博风**：Y2K 像素界面、仿 OS 窗口，记录也可以很可爱。
- 💊 **用药记录（核心）**：记录药品种类、片数、时间、公开/私密设置，沉淀属于自己的用药档案。
- ✍️ **心得分享**：每条记录都能写下真实感受与反思，形成可回溯的「用药 × 心路」轨迹。
- 🧩 **用药组合一键登记**：把多种药 + 剂量的常用方案存成模板，需要时一键批量登记。
- 🚨 **剂量风险预警**：如实标记剂量，系统自动识别**超量（原「过量」）风险**并标红警示，提醒及时关注与调整。
- 📊 **风险数据看板**：近 7 / 14 / 30 天风险次数、本周 vs 上周趋势、连续记录天数，客观看见自己的变化。
- 📦 **药品库存管理**：记录每种药的剩余片数，避免断药或误服。
- 🌐 **社区共享流**：私密记录只属于自己；也可以主动公开，与全站伙伴的真诚分享相遇。
- 💬 **点赞 & 评论**：在彼此的记录下温柔互动、互相支持。
- 📌 **备忘录**：随手记录、随时回顾。
- 👤 **登录 / 注册**：专属账号、数据隔离、安全可靠。

---

## 🌐 在线演示

> 🔗 临时公网地址：**https://candied-dutiful-schilling.ngrok-free.dev**
> 账号：`tester01` / `pass1234`（可自行注册新账号）

> ⚠️ 说明：该地址由 ngrok 免费隧道提供，**仅在演示时临时开放**；我的电脑关机或隧道关闭后即失效。本项目定位为学习/展示用途，正式部署上线后可提供长期稳定的访问地址。

---


## 🛠️ 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 17 · Spring Boot |
| 持久层 | Spring Data JPA · Hibernate |
| 数据库 | MySQL |
| JSON | Jackson（含 JSR-310 时间序列化） |
| 前端 | 原生 HTML / CSS / JavaScript（无框架） |

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven（项目自带 `mvnw` 包装器）
- MySQL 8.x

### 1. 准备数据库

启动 MySQL 并创建数据库（JPA 会 `ddl-auto=update` 自动建表）：

```sql
CREATE DATABASE cyber_tunnel_db;
```

### 2. 配置数据库连接

配置默认从 `src/main/resources/application.properties` 读取，支持环境变量覆盖（推荐，避免把密码提交到 GitHub）：

```properties
# 若设置了环境变量则优先使用；否则使用默认值（本地开发）
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/cyber_tunnel_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:你的密码}
```

方式一：直接改 `application.properties` 中的默认值（本地开发最快）。

方式二：用环境变量（部署时推荐）：

```bash
# Windows PowerShell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的密码"

# macOS / Linux
export DB_USERNAME=root DB_PASSWORD=你的密码
```

### 3. 启动后端

```bash
# Windows
mvnw spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

后端默认运行在 **http://localhost:8080**

### 4. 使用页面

- 社区主页：**http://localhost:8080**（未登录会自动跳转到登录页）
- 登录 / 注册页：**http://localhost:8080/login.html**

在登录页注册一个新账号即可进入社区。

---

## 📖 功能一览

### 💊 用药记录（核心）

- 填写药品种类、本次片数、吃药时间、分享设置（私密 / 公开）。
- 每一条记录都可以写下一段 **心得描述**：今天为什么吃、吃完感觉怎么样、有什么想对自己说的。
- 按实际情况勾选「本次为遵医嘱正常剂量」；如果确有超量，如实标记后，系统会以 **剂量风险预警** 的方式标红提醒，而不是回避问题。

### ✍️ 心得与成长轨迹

- 每条公开 / 私密记录下都有心得框，随时补充真实感受。
- 历史心得自动沉淀，构成只属于你自己的「用药 × 心路」时间线，回头翻看会很有意义。

### 🧩 用药方案（组合）

- 创建「方案名 + 药名 × 片数」的组合模板，比如一套常用搭配。
- 「一键登记」把整套方案按当前时间批量记录成正常用药。
- 支持删除单个方案 / 清空全部。

### 🚨 剂量风险预警与看板

- 识别超量风险后自动标红、附上 ⚠️ 徽章提醒。
- 风险看板展示近 7 / 14 / 30 天次数、本周 vs 上周趋势对比、连续记录天数，帮助自己客观观察变化。

### 🌐 社区共享流

- 「私人」页签：只看见自己的记录。
- 「公开」页签：浏览全员最新健康动态，互相点赞、评论，感受「原来不止我这样」。

### 📌 备忘录

- 写记录 / 历史记录双页签，按时间倒序展示，支持逐条删除或一键清空。

### 📦 药品库存

- 每种药品记录剩余片数，服用自动扣减、支持手动补货调整，避免断药或重复购买。

### 🔒 用户系统

- 注册、登录、记住当前用户、退出登录；数据按用户隔离，隐私默认安全。

---

## 📚 文档

- 🔐 [登录态与权限安全改造记录](docs/AUTH-SECURITY.md)：从「前端传 userId」升级为「后端 token 认证」的完整思路，含面试问答提纲。

---


## 📁 项目结构

```
medicine-box-main
├─ pom.xml
├─ mvnw / mvnw.cmd
└─ src/main
   ├─ java/com/cybertunnel
   │  ├─ controller/   # 各业务 REST 控制器
   │  ├─ service/      # 业务逻辑层
   │  ├─ model/        # JPA 实体
   │  ├─ repository/   # Spring Data 仓库
   │  └─ config/       # 配置（Jackson 等）
   └─ resources
      ├─ application.properties   # 数据库等配置
      └─ static/                 # 前端页面与素材（index.html / login.html 等）
```

### 主要 API

| 方法 & 路径 | 说明 |
| --- | --- |
| `POST /api/users/register` | 注册用户 |
| `POST /api/users/login` | 登录 |
| `GET /api/users` | 用户列表（昵称映射用） |
| `GET/POST/PATCH/DELETE /api/medications...` | 用药记录增删改查、心得更新、归档 |
| `GET /api/medications/public` | 公开记录流 |
| `GET /api/medications/stats/risk` | 剂量风险看板统计 |
| `POST /api/medications/archive` | 一键归档 |
| `GET/POST/DELETE /api/plans...` | 用药方案管理 |
| `GET/POST /api/memos`, `DELETE /api/memos` | 备忘录管理 |
| `POST /api/social/like`, `GET /api/social/like/status` | 点赞 / 状态 |
| `GET/POST/DELETE /api/social/comments` | 评论管理 |
| `GET/POST /api/inventory`, `PUT /api/inventory/{name}` | 药品库存管理 |

---

## 🌱 项目意义与未来展望

Cyber-Tunnel 不只是一份作业，它源于一个真实的想法：**很多需要长期用药的人（尤其是青少年）并不缺少药，缺少的是被理解、被支持的安全感。** 我们想让「记录用药」不再像交差，而是像对自己负责。

接下来的方向：

1. **更专业的用药陪伴**
   - 用药间隔 / 重复用药提醒，个人健康月报。
   - 风险数据可**脱敏后用于健康科普与公益研究**，让数据产生公共价值。
2. **更暖的社区连接**
   - 匿名互助小组、康复故事、结构化打卡。
   - 关注 / 好友 / 私信，构建彼此支持的健康圈。
3. **隐私与安全优先**
   - 记录公开到哪一级完全由用户掌控。
   - 敏感健康数据加密存储与合规审计（个人信息保护）。

> ⚠️ **免责声明**：本项目是个人学习 / 自我管理 / 同伴支持工具，**不构成任何医疗建议**。用药请遵医嘱；如遇疑似过量或紧急情况，请立即联系专业医疗机构或拨打急救电话。

---

## 📄 License

本项目目前为个人学习 / 展示用途，尚未指定开源协议。如有意合作或开源，欢迎联系作者。

<p align="center"><sub>Made with 💖 · Cyber-Tunnel · 用药记录与心得分享社区</sub></p>

