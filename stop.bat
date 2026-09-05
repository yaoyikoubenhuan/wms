@echo off
chcp 65001 >nul
title WMS Stopper
echo ============================================
echo   工作管理系统（WMS）一键关闭
echo ============================================
echo.

set "KILLED=0"

REM ---------- 1. 按服务窗口标题关闭整个进程树 ----------
for %%T in ("WMS-Backend" "WMS-Frontend") do (
    tasklist /FI "WINDOWTITLE eq %%~T*" /NH 2>nul | findstr /I "cmd.exe" >nul
    if not errorlevel 1 (
        taskkill /FI "WINDOWTITLE eq %%~T*" /T /F >nul 2>nul
        echo 已关闭服务窗口：%%~T
        set "KILLED=1"
    )
)

REM ---------- 2. 兜底：按端口查杀监听进程 ----------
for %%P in (8080 5173) do (
    for /f "tokens=5" %%I in ('netstat -ano ^| findstr ":%%P " ^| findstr LISTENING') do (
        echo 端口 %%P 仍被进程 %%I 占用，正在结束...
        taskkill /PID %%I /T /F >nul 2>nul
        set "KILLED=1"
    )
)

REM ---------- 3. 确认端口已释放 ----------
set /a LEFT=0
for %%P in (8080 5173) do (
    netstat -ano | findstr ":%%P " | findstr LISTENING >nul
    if not errorlevel 1 (
        echo [警告] 端口 %%P 仍被占用，请手动检查。
        set /a LEFT+=1
    )
)

echo.
if "%KILLED%"=="0" (
    echo 未检测到正在运行的 WMS 服务。
) else if "%LEFT%"=="0" (
    echo 所有服务已关闭，端口 8080 / 5173 已释放。
)
echo.
pause
