@echo off
echo �������������ѡ�dev, test,prd�е�һ��
set /p env=>nul 
echo ������jobName��
set /p jobName=>nul 
echo jobName��%jobName%
cd %~dp0
call mvn clean package -Denv=%env% -Dmaven.test.skip=true -Djob-name=%jobName%
pause