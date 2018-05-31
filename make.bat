@echo off
echo 请输入编译配置选项：dev, test,prd中的一项
set /p env=>nul 
echo 请输入jobName：
set /p jobName=>nul 
echo jobName：%jobName%
cd %~dp0
call mvn clean package -Denv=%env% -Dmaven.test.skip=true -Djob-name=%jobName%
pause