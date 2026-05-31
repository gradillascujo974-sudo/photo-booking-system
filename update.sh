#!/bin/bash
# 摄影约拍系统 - 一键更新部署脚本
# 用法：在服务器上执行 bash update.sh

set -e

echo ">>> 拉取最新代码..."
git pull origin master

echo ">>> 构建前端..."
npm install --silent
npm run build

echo ">>> 构建后端..."
cd backend
./mvnw package -DskipTests -q
cd ..

echo ">>> 重启后端服务..."
pkill -f "photo-booking-backend" || echo "无旧进程"
sleep 2
nohup java -jar backend/target/photo-booking-backend-1.0.0.jar > backend/app.log 2>&1 &

echo ">>> 后端日志: tail -f backend/app.log"
echo ">>> 更新完成！"
