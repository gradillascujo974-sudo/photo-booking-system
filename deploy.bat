@echo off
chcp 65001 >nul
echo ============================================
echo   光影约拍 - 一键部署到服务器
echo ============================================
echo.

set "PROJECT_DIR=%~dp0"
set "SERVER_IP=106.54.36.113"
set "SERVER_USER=root"
set "SERVER_PASS=Lym010812.,."

cd /d "%PROJECT_DIR%"

echo [1/4] 构建前端...
call npm run build
if errorlevel 1 (
    echo 前端构建失败！
    pause
    exit /b 1
)

echo [2/4] 打包部署文件...
if exist deploy.tar.gz del deploy.tar.gz
tar -czf deploy.tar.gz dist/ backend/target/photo-booking-backend-1.0.0.jar
echo 打包完成: %~dp0deploy.tar.gz

echo [3/4] 上传到服务器...
python -c "
import paramiko, sys
print('  上传中(约30MB)...')
t = paramiko.Transport(('%SERVER_IP%', 22))
t.connect(username='%SERVER_USER%', password='%SERVER_PASS%')
sftp = paramiko.SFTPClient.from_transport(t)
sftp.put(r'%PROJECT_DIR%deploy.tar.gz', '/root/deploy.tar.gz')
print('  上传完成，解压中...')
chan = t.open_session()
chan.exec_command('cd /root && tar xzf deploy.tar.gz -C photo-booking-system/ && cp -r photo-booking-system/dist/* /var/www/photo-booking/ && echo DEPLOY_OK')
chan.settimeout(30)
out = chan.recv(4096).decode()
print(out.strip())
sftp.close()
t.close()
" 2>&1

if errorlevel 1 (
    echo 上传失败！
    pause
    exit /b 1
)

echo [4/4] 重启后端服务...
python -c "
import paramiko
t = paramiko.Transport(('%SERVER_IP%', 22))
t.connect(username='%SERVER_USER%', password='%SERVER_PASS%')
chan = t.open_session()
chan.exec_command('pkill -f photo-booking-backend; sleep 2; cd /root/photo-booking-system/backend && nohup java -jar target/photo-booking-backend-1.0.0.jar > app.log 2>&1 & sleep 3 && echo STARTED')
chan.settimeout(15)
out = chan.recv(4096).decode()
print(out.strip())
t.close()
print('后端已重启')
" 2>&1

if errorlevel 1 (
    echo 后端重启失败！
    pause
    exit /b 1
)

echo.
echo ============================================
echo   部署完成！
echo   访问: http://jxwedphoto.com
echo ============================================
pause
