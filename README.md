# 光影约拍 - 摄影约拍系统

基于毕业设计论文《基于 Springboot+Vue3 的摄影约拍系统》实现的前端演示网站。

## 技术栈

**前端**
- Vue 3 + Vite + Element Plus + Vue Router + Axios

**后端**（`backend/`）
- Spring Boot 3 + MyBatis-Plus
- H2 文件数据库（默认，开箱即用）或 MySQL

## 功能模块（对应论文）

| 模块 | 说明 |
|------|------|
| 多角色登录 | 普通用户 / 摄影师 / 管理员 |
| 约拍服务 | 分类展示、风格/价格筛选、在线预约 |
| 摄影师 | 认证标识、风格与城市筛选 |
| 作品集 | 浏览、点赞、取消点赞 |
| 订单 | 下单扣款、状态流转、取消退款、评价 |
| 摄影师认证 | 用户提交 → 管理员审核 |
| 摄影师后台 | 接单、完成订单、服务与作品管理 |
| 管理后台 | 用户、认证、订单、公告 |

## 演示账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 普通用户 | user123 | 123456 |
| 摄影师 | photo01 | 123456 |
| 管理员 | admin | admin123 |

普通用户初始余额 ¥2000，可直接预约服务。

## 快速开始

**1. 启动后端（必须先启动）**

```bash
cd backend
mvn spring-boot:run
```

后端运行在 http://localhost:8080

**2. 启动前端**

```bash
npm install
npm run dev
```

浏览器访问 http://localhost:5173（已通过 Vite 代理转发 `/api` 到后端）

> 数据持久化在 `backend/data/`（H2）或 MySQL 中，订单、点赞、充值等操作会真实写入数据库。

## 构建

```bash
npm run build
npm run preview
```

## 后端详细说明

见 [backend/README.md](backend/README.md)
