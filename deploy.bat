@echo off
chcp 65001 >nul
cd /d "%~dp0"

REM Try to find Python - check common locations
set PYTHON=
where python >nul 2>&1 && set PYTHON=python
if "%PYTHON%"=="" if exist "C:\Users\logwe\AppData\Local\Programs\Python\Python312\python.exe" set PYTHON=C:\Users\logwe\AppData\Local\Programs\Python\Python312\python.exe
if "%PYTHON%"=="" (
    echo [X] 未找到 Python，请确认已安装 Python 3
    pause
    exit /b 1
)

"%PYTHON%" deploy.py
pause
