#!/bin/bash

echo "========================================"
echo "Iniciando Sistema de Farmácia"
echo "========================================"
echo ""

# Inicia o backend
echo "[1/2] Iniciando Backend (Spring Boot)..."
echo "Porta: 8080"
echo ""
cd "$(dirname "$0")"
gnome-terminal --title="Backend - Farmácia" -- bash -c "./mvnw spring-boot:run; exec bash" &

# Aguarda o backend iniciar
echo "Aguardando backend iniciar..."
sleep 15

# Inicia o frontend
echo ""
echo "[2/2] Iniciando Frontend (React)..."
echo "Porta: 3000"
echo ""
cd "../DesafioJava-farmacia-front/app-farmacia"
gnome-terminal --title="Frontend - Farmácia" -- bash -c "npm start; exec bash" &

echo ""
echo "========================================"
echo "Sistema iniciando..."
echo ""
echo "Backend:  http://localhost:8080"
echo "Frontend: http://localhost:3000"
echo "Swagger:  http://localhost:8080/swagger-ui.html"
echo ""
echo "Credenciais de Login:"
echo "Email: admin@farmacia.com"
echo "Senha: admin123"
echo "========================================"
echo ""
