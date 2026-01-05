#!/bin/bash

echo "==================================="
echo "Sistema de Farmacia - API REST"
echo "==================================="
echo ""

echo "Verificando Java..."
java -version
if [ $? -ne 0 ]; then
    echo "ERRO: Java não encontrado!"
    echo "Por favor, instale o Java 25 LTS"
    exit 1
fi
echo ""

echo "Verificando PostgreSQL..."
echo "IMPORTANTE: Certifique-se de que o PostgreSQL está rodando!"
echo "            E que o banco 'farmacia_db' foi criado."
echo ""
read -p "Pressione Enter para continuar..."

echo ""
echo "Compilando e executando a aplicação..."
echo ""

./mvnw clean spring-boot:run
