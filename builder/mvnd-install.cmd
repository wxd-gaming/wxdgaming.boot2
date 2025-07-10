@echo off

:: 设置Java环境变量
set JAVA_HOME=C:\java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

:: 检查JAVA_HOME是否配置成功
echo %JAVA_HOME%
echo %PATH%

:: 打印Java版本信息，确认配置成功
java -version

C:\java\maven-mvnd-1.0.2-windows-amd64\\bin\\mvnd.cmd clean compile -T 1C test -Dmaven.compile.fork=true -DfailIfNoTests=false install