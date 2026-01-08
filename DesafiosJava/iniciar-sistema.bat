@echo off
echo ========================================
echo Iniciando Sistema de Farmacia
echo ========================================
echo.

REM Verifica se o PostgreSQL está rodando
echo Verificando PostgreSQL...
timeout /t 2 /nobreak > nul

REM Inicia o backend
echo.
echo [1/2] Iniciando Backend (Spring Boot)...
echo Porta: 8080
echo.
cd /d "%~dp0"
start "Backend - Farmacia" cmd /k "mvnw.cmd spring-boot:run"

REM Aguarda o backend iniciar
echo Aguardando backend iniciar...
timeout /t 15 /nobreak > nul

REM Inicia o frontend
echo.
echo [2/2] Iniciando Frontend (React)...
echo Porta: 3000
echo.
cd /d "%~dp0..\DesafioJava-farmacia-front\app-farmacia"
start "Frontend - Farmacia" cmd /k "npm start"

echo.
echo ========================================
echo Sistema iniciando...
echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:3000
echo Swagger:  http://localhost:8080/swagger-ui.html
echo.
echo Credenciais de Login:
echo Email: admin@farmacia.com
echo Senha: admin123
echo ========================================
echo.
pause
