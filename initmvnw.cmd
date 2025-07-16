@echo off

:: 设置Java环境变量
set JAVA_HOME=C:\java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

:: 检查JAVA_HOME是否配置成功
echo %JAVA_HOME%

:: 打印Java版本信息，确认配置成功
call java -version

C:\java\apache-maven-3.9.9\\bin\\mvn -N io.takari:maven:wrapper