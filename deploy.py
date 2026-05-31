#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""光影约拍 - 一键部署脚本"""
import os, sys, subprocess, paramiko

PROJECT_DIR = os.path.dirname(os.path.abspath(__file__))
SERVER_IP = "106.54.36.113"
SERVER_USER = "root"
SERVER_PASS = "Lym010812.,."

def run_cmd(args, desc):
    print(f"[{desc}]")
    result = subprocess.run(args, shell=True, cwd=PROJECT_DIR)
    if result.returncode != 0:
        print(f"[X] {desc} 失败！")
        sys.exit(1)

def ssh_exec(host, user, password, cmd, desc=""):
    if desc:
        print(f"[{desc}]")
    t = paramiko.Transport((host, 22))
    t.connect(username=user, password=password)
    chan = t.open_session()
    chan.exec_command(cmd)
    chan.settimeout(60)
    out = chan.recv(8192).decode()
    err = chan.recv_stderr(8192).decode()
    if out:
        print(out.strip())
    if err:
        print(err.strip())
    t.close()

def sftp_upload(host, user, password, local, remote, desc=""):
    if desc:
        print(f"[{desc}]")
    t = paramiko.Transport((host, 22))
    t.connect(username=user, password=password)
    sftp = paramiko.SFTPClient.from_transport(t)
    sftp.put(local, remote)
    sftp.close()
    t.close()
    print("  上传完成")

print("=" * 50)
print("  光影约拍 - 一键部署")
print("=" * 50)

# 1. Build frontend
print("\n[1/4] 构建前端...")
run_cmd("npm run build", "前端构建")

# 2. Package
print("\n[2/4] 打包部署文件...")
tar_file = os.path.join(PROJECT_DIR, "deploy.tar.gz")
if os.path.exists(tar_file):
    os.remove(tar_file)

print(f"  打包到项目目录...")
run_cmd('tar -czf deploy.tar.gz dist/ backend/target/photo-booking-backend-1.0.0.jar', "打包")
size_mb = os.path.getsize(tar_file) / 1024 / 1024
print(f"  打包完成: {size_mb:.1f} MB")

# 3. Upload
print(f"\n[3/4] 上传到服务器 {SERVER_IP}...")
sftp_upload(SERVER_IP, SERVER_USER, SERVER_PASS,
            tar_file, "/root/deploy.tar.gz", "上传")

ssh_exec(SERVER_IP, SERVER_USER, SERVER_PASS,
    "cd /root && tar xzf deploy.tar.gz -C photo-booking-system/ && "
    "cp -r photo-booking-system/dist/* /var/www/photo-booking/ && "
    "echo '前端已更新'",
    "解压部署")

# 4. Restart backend
print("\n[4/4] 重启后端服务...")
ssh_exec(SERVER_IP, SERVER_USER, SERVER_PASS,
    "pkill -f photo-booking-backend; "
    "sleep 2; "
    "cd /root/photo-booking-system/backend && "
    "nohup java -jar target/photo-booking-backend-1.0.0.jar > app.log 2>&1 & "
    "sleep 3 && "
    "curl -s http://127.0.0.1:8080/api/categories | head -c 100",
    "重启后端")

# Cleanup
if os.path.exists(tar_file):
    os.remove(tar_file)

print("\n" + "=" * 50)
print("  部署完成！")
print("  访问: http://jxwedphoto.com")
print("=" * 50)
