#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""光影约拍 - 一键部署脚本（纯 Python 实现，不依赖外部 tar 命令）"""
import os, sys, subprocess, paramiko, tarfile

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

def make_tar_gz(output_path, source_dirs):
    """用 Python 内置库创建 tar.gz，不依赖外部命令"""
    print("  打包中...")
    with tarfile.open(output_path, "w:gz") as tar:
        for item in source_dirs:
            src = os.path.join(PROJECT_DIR, item)
            if os.path.isdir(src):
                tar.add(src, arcname=item)
            elif os.path.isfile(src):
                tar.add(src, arcname=item)
            else:
                print(f"  警告: {item} 不存在，跳过")

print("=" * 50)
print("  光影约拍 - 一键部署")
print("=" * 50)

# 1. Build frontend
print("\n[1/4] 构建前端...")
run_cmd("npm run build", "前端构建")

# 2. Package (using Python's built-in tarfile, no external tar needed)
print("\n[2/4] 打包部署文件...")
tar_file = os.path.join(PROJECT_DIR, "deploy.tar.gz")
if os.path.exists(tar_file):
    os.remove(tar_file)

make_tar_gz(tar_file, [
    "dist",
    "backend/target/photo-booking-backend-1.0.0.jar"
])
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

# 4. Restart backend via systemd (auto-restart on crash)
print("\n[4/4] 重启后端服务...")
ssh_exec(SERVER_IP, SERVER_USER, SERVER_PASS,
    "systemctl restart photo-booking; "
    "sleep 5 && "
    "curl -s http://127.0.0.1:8080/api/categories | head -c 100",
    "重启后端")

# Cleanup
if os.path.exists(tar_file):
    os.remove(tar_file)

print("\n" + "=" * 50)
print("  部署完成！")
print("  访问: http://jxwedphoto.com")
print("=" * 50)
