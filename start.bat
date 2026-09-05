@echo off
chcp 65001 >nul
title WMS Launcher
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo ============================================
echo   工作管理系统（WMS）一键启动
echo ============================================
echo.

REM ---------- 1. 环境检查 ----------
echo [1/4] 检查运行环境...

where java >nul 2>nul
if errorlevel 1 (
    echo [错误] 未检测到 Java，请先安装 JDK 8+ 并配置 PATH。
    pause
    exit /b 1
)

set "USE_MVN=1"
where mvn >nul 2>nul
if errorlevel 1 (
    set "USE_MVN=0"
    if not exist "backend\target\work-management-system-backend-1.0.0.jar" (
        echo [错误] 未检测到 Maven，且 backend\target 下没有可运行 jar。
        echo        请安装 Maven，或先执行：cd backend ^&^& mvn package -DskipTests
        pause
        exit /b 1
    )
    echo [提示] 未检测到 Maven，将使用已构建的 jar 启动后端。
)

where npm >nul 2>nul
if errorlevel 1 (
    echo [错误] 未检测到 npm，请先安装 Node.js（含 npm）并配置 PATH。
    pause
    exit /b 1
)

netstat -ano | findstr ":3306 " | findstr LISTENING >nul
if errorlevel 1 (
    echo [警告] 未检测到 MySQL 服务（端口 3306）。
    echo        请确认 MySQL 已启动，且已执行 backend\src\main\resources\schema.sql 初始化数据库。
    echo.
)

REM ---------- 2. 释放可能被占用的端口 ----------
echo [2/4] 检查端口占用（8080 / 5173）...
for %%P in (8080 5173) do (
    for /f "tokens=5" %%I in ('netstat -ano ^| findstr ":%%P " ^| findstr LISTENING') do (
        echo        端口 %%P 被进程 %%I 占用，正在释放...
        taskkill /PID %%I /F >nul 2>nul
    )
)

REM ---------- 3. 启动后端 ----------
echo [3/4] 启动后端服务（端口 8080）...
if "%USE_MVN%"=="1" (
    start "WMS-Backend" /D "%~dp0backend" cmd /k mvn spring-boot:run
) else (
    start "WMS-Backend" /D "%~dp0backend" cmd /k java -jar target\work-management-system-backend-1.0.0.jar
)

REM ---------- 4. 启动前端 ----------
echo [4/4] 启动前端服务（端口 5173）...
start "WMS-Frontend" /D "%~dp0frontend" cmd /k "(if not exist node_modules\.bin\vite.cmd npm install) && npm run dev"

REM ---------- 等待前端就绪后打开浏览器 ----------
echo.
echo 服务启动中，请稍候（首次启动需下载依赖，可能需要几分钟）...
set /a COUNT=0
:WAIT_FRONT
timeout /t 3 /nobreak >nul
set /a COUNT+=3
netstat -ano | findstr ":5173 " | findstr LISTENING >nul
if not errorlevel 1 goto READY
if %COUNT% GEQ 180 (
    echo [提示] 等待超时，请手动查看两个服务窗口的日志后访问 http://localhost:5173
    goto END
)
goto WAIT_FRONT

:READY
echo.
echo ============================================
echo   启动完成！
echo   前端地址：http://localhost:5173
echo   后端接口：http://localhost:8080
echo   默认账号：admin / admin123
echo   关闭服务请运行 stop.bat
echo ============================================
start http://localhost:5173

:END
echo.
pause
