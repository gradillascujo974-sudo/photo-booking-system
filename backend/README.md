# 摄影约拍系统 - 后端

Spring Boot 3 + MyBatis-Plus + MySQL/H2

## 环境要求

- JDK 17+
- Maven 3.8+

## 快速启动（默认 H2 文件数据库，无需安装 MySQL）

```bash
cd backend
mvn spring-boot:run
```

服务地址：http://localhost:8080  
H2 控制台：http://localhost:8080/h2-console（JDBC URL: `jdbc:h2:file:./data/photo_booking`）

数据保存在 `backend/data/` 目录，重启后数据保留。

## 使用 MySQL

1. 创建数据库：`CREATE DATABASE photo_booking DEFAULT CHARSET utf8mb4;`
2. 修改 `application.yml` 中 `spring.profiles.active` 为 `mysql`，并配置账号密码
3. 启动：`mvn spring-boot:run -Dspring-boot.run.profiles=mysql`

## API 说明

- 基础路径：`/api`
- 登录后请求头携带：`X-User-Id: {用户id}`

主要接口：

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/login | 登录 |
| POST | /api/register | 注册 |
| GET | /api/services | 服务列表 |
| POST | /api/orders | 创建订单 |
| PUT | /api/orders/{id}/status | 更新订单状态 |
| POST | /api/likes/toggle/{portfolioId} | 点赞切换 |
| POST | /api/identify | 提交认证 |
| PUT | /api/identify/{id}/audit | 审核认证 |

## 演示账号

与前端一致：user123 / photo01 / admin
