@echo off
chcp 65001
setlocal enabledelayedexpansion

set "domain="
set /p domain=请提供一个顶级域名，例如 example.com: 

:: 根据 '.' 分割域名
for /f "tokens=1,2 delims=." %%a in ("%domain%") do (
    set "subdomain=%%a"
    set "tld=%%b"
)

:: 确认分割结果
if "%subdomain%"=="" (
    echo 无法解析域名的次级域名部分，请检查输入。
    pause > nul
    exit /b
)

if "%tld%"=="" (
    echo 无法解析域名的顶级域名部分，请检查输入。
    pause > nul
    exit /b
)

:: 输出分割结果
echo 一级域名: %subdomain%
echo 顶级域名: %tld%
echo 域名反写结果：%tld%.%subdomain%

:: 提示用户确认
set "confirm="
set /p confirm=是否继续重命名文件夹? 按 Y 确认, 其他键取消: 
if /i not "%confirm%"=="Y" (
    echo 操作已取消。
    pause > nul
    exit /b
)

:: 定义根目录（当前文件夹）
set "root_dir=%cd%"

:: 第一次循环：重命名所有的 uncarbon 文件夹为一级域名
for /d /r "%root_dir%" %%i in (uncarbon) do (
    if exist "%%i" (
        echo 正在将 %%i 重命名为 %subdomain%
        ren "%%i" "%subdomain%"
    )
)

:: 第二次循环：重命名所有的 cc 文件夹为顶级域名
for /d /r "%root_dir%" %%i in (cc) do (
    if exist "%%i" (
        echo 正在将 %%i 重命名为 %tld%
        ren "%%i" "%tld%"
    )
)

echo 所有文件夹已重命名完成！
pause

echo 操作完成。
pause>nul