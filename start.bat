@echo off
setlocal enabledelayedexpansion

REM Set Java 21 path
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"

REM Add Java bin to PATH
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Verify Java version
echo ========================================
echo   Spring Boot API - Tienda
echo   Java 21 + MySQL
echo ========================================
echo.
echo Using Java from: %JAVA_HOME%
java -version
echo.

REM Run the Spring Boot application
echo Starting Spring Boot application...
.\mvnw spring-boot:run