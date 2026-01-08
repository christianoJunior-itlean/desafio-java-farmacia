# 🎉 Backend Configurado - Pronto para Integração com Frontend

## ✅ Mudanças Implementadas com Sucesso

### 📁 Arquivos Criados

1. **`config/CorsConfig.java`** ✨ NOVO
   - Configuração CORS completa
   - Permite requisições do React (localhost:3000)
   - Suporte a todos os métodos HTTP necessários
   - Headers de autorização configurados

2. **`config/DataInitializer.java`** ✨ NOVO
   - Cria usuário admin automaticamente
   - Executa ao iniciar a aplicação
   - Credenciais padrão para testes

3. **`iniciar-sistema.bat`** ✨ NOVO (Windows)
   - Script para iniciar backend + frontend juntos
   - Um clique para rodar tudo

4. **`iniciar-sistema.sh`** ✨ NOVO (Linux/Mac)
   - Versão Unix do script de inicialização

### 📝 Arquivos Modificados

1. **`config/SecurityConfig.java`** 🔧 MODIFICADO
   - Integração com CorsConfig
   - Configuração `.cors()` adicionada
   - Mantém todas as funcionalidades de segurança

2. **`resources/application.properties`** 🔧 MODIFICADO
   - Seção CORS adicionada
   - Configuração de origens permitidas
   - Headers e métodos configurados

---

## 🚀 Início Rápido

### Opção 1: Script Automático (Recomendado)

**Windows:**
```bash
cd DesafiosJava
iniciar-sistema.bat
```

**Linux/Mac:**
```bash
cd DesafiosJava
chmod +x iniciar-sistema.sh
./iniciar-sistema.sh
```

### Opção 2: Manual

**Terminal 1 - Backend:**
```bash
cd DesafiosJava
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd DesafioJava-farmacia-front/app-farmacia
npm start
```

---

## 🔐 Credenciais de Acesso

O sistema criará automaticamente um usuário administrador:

- **Email:** `admin@farmacia.com`
- **Senha:** `admin123`

⚠️ **IMPORTANTE:** Esta senha é apenas para desenvolvimento. Altere em produção!

### Como o usuário é criado?

1. Ao iniciar o backend, o `DataInitializer` executa
2. Verifica se o usuário `admin@farmacia.com` já existe
3. Se não existir, cria com a senha `admin123`
4. Exibe as credenciais no console

**Exemplo de log:**
```
========================================
Usuário administrador criado com sucesso!
========================================
Email: admin@farmacia.com
Senha: admin123
========================================
```

---

## 🔗 URLs do Sistema

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **Frontend** | http://localhost:3000 | Interface React |
| **Backend API** | http://localhost:8080 | API REST |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Documentação interativa |
| **OpenAPI Docs** | http://localhost:8080/v3/api-docs | Especificação OpenAPI |

---

## 🛠️ Configuração CORS Detalhada

### Origens Permitidas
```
✅ http://localhost:3000
✅ http://127.0.0.1:3000
✅ http://localhost:3001
```

### Métodos HTTP Permitidos
```
✅ GET     - Listar/Consultar
✅ POST    - Criar
✅ PUT     - Atualizar (completo)
✅ PATCH   - Atualizar (parcial)
✅ DELETE  - Deletar
✅ OPTIONS - Preflight requests
```

### Headers Permitidos
```
✅ Authorization     - Token JWT
✅ Content-Type      - Tipo do conteúdo
✅ Accept           - Formato aceito
✅ Origin           - Origem da requisição
✅ Access-Control-* - Headers CORS
```

### Características
- ✅ **Credentials:** Habilitado (permite cookies e auth headers)
- ✅ **Max Age:** 3600 segundos (1 hora de cache para preflight)
- ✅ **Exposed Headers:** Authorization, Content-Type

---

## 🧪 Testes de Integração

### 1. Testar Backend Isoladamente

**Teste de Health Check:**
```bash
curl http://localhost:8080/actuator/health
```

**Teste de Login:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@farmacia.com",
    "senha": "admin123"
  }'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmYXJtYWNpYS5jb20i...",
  "tipo": "Bearer",
  "username": "admin@farmacia.com"
}
```

### 2. Testar CORS

**Teste Preflight:**
```bash
curl -X OPTIONS http://localhost:8080/medicamentos \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Authorization" \
  -i
```

**Headers esperados na resposta:**
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

### 3. Testar com Token

**1. Faça login e copie o token:**
```bash
TOKEN=$(curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@farmacia.com","senha":"admin123"}' \
  | jq -r '.token')

echo $TOKEN
```

**2. Use o token para acessar endpoint protegido:**
```bash
curl http://localhost:8080/medicamentos \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Testar Frontend + Backend

1. **Abra o DevTools (F12)**
2. **Vá para aba Network**
3. **Faça login no frontend**
4. **Verifique as requisições:**
   - Request Headers devem ter: `Authorization: Bearer {token}`
   - Response Headers devem ter: `Access-Control-Allow-Origin`

---

## 🐛 Troubleshooting

### ❌ Erro: "CORS policy: No 'Access-Control-Allow-Origin'"

**Causa:** Backend não está enviando headers CORS

**Solução:**
1. Verifique se o backend foi reiniciado após as mudanças
2. Verifique se `CorsConfig.java` existe em `config/`
3. Verifique logs do backend para erros

**Verificar CORS manualmente:**
```bash
curl -i -X OPTIONS http://localhost:8080/medicamentos \
  -H "Origin: http://localhost:3000"
```

Deve retornar headers `Access-Control-*`

---

### ❌ Erro: "401 Unauthorized" no login

**Possíveis causas:**

**1. Usuário não foi criado:**
```bash
# Verifique os logs do backend ao iniciar
# Procure por: "Usuário administrador criado"
```

**2. Credenciais incorretas:**
- Email: `admin@farmacia.com` (exato)
- Senha: `admin123` (exato)

**3. Banco de dados não está conectado:**
```bash
# Verifique se PostgreSQL está rodando
# Windows: services.msc → PostgreSQL
# Linux: systemctl status postgresql
```

**Solução:**
```bash
# 1. Pare o backend (Ctrl+C)
# 2. Verifique PostgreSQL
# 3. Reinicie o backend
./mvnw spring-boot:run
```

---

### ❌ Erro: "Connection refused" ou "ERR_CONNECTION_REFUSED"

**Causa:** Backend não está rodando

**Solução:**
```bash
# Verifique se o backend está rodando
curl http://localhost:8080

# Se não estiver, inicie:
cd DesafiosJava
./mvnw spring-boot:run
```

**Verificar porta:**
```bash
# Windows
netstat -ano | findstr :8080

# Linux/Mac
lsof -i :8080
```

---

### ❌ Erro: "403 Forbidden" após login

**Causa:** Token JWT não está sendo enviado ou é inválido

**Verificar no Frontend:**
1. Abra DevTools (F12)
2. Vá para Application → Local Storage
3. Verifique se há uma chave `token`
4. Vá para Network → Headers
5. Verifique se tem: `Authorization: Bearer {token}`

**Solução:**
```javascript
// Limpe o localStorage e faça login novamente
localStorage.clear();
// Recarregue a página e faça login
```

---

### ❌ Banco de dados não conecta

**Erro típico:**
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

**Soluções:**

**1. Verificar se PostgreSQL está rodando:**
```bash
# Windows
services.msc
# Procure: PostgreSQL

# Linux
sudo systemctl status postgresql
sudo systemctl start postgresql
```

**2. Verificar credenciais:**
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/farmacia_db
spring.datasource.username=postgres
spring.datasource.password=loxmeyes99  # ALTERE SE NECESSÁRIO
```

**3. Criar banco se não existir:**
```bash
psql -U postgres
CREATE DATABASE farmacia_db;
\q
```

---

### ❌ Porta 8080 já em uso

**Erro:**
```
Port 8080 is already in use
```

**Solução:**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID [PID_NUMBER] /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9

# OU altere a porta no application.properties
server.port=8081
```

---

## 📊 Fluxo de Autenticação

```
1. Frontend envia credenciais
   POST /auth/login
   { username, senha }
        ↓
2. Backend valida no banco de dados
   BCrypt.matches(senha, senhaCriptografada)
        ↓
3. Se válido, gera JWT Token
   JwtUtil.generateToken(username)
        ↓
4. Retorna token para frontend
   { token, tipo: "Bearer", username }
        ↓
5. Frontend armazena em localStorage
   localStorage.setItem('token', token)
        ↓
6. Requisições seguintes incluem token
   Authorization: Bearer {token}
        ↓
7. Backend valida token em cada requisição
   JwtAuthenticationFilter intercepta
   JwtUtil.validateToken(token)
        ↓
8. Se válido → processa requisição
   Se inválido → 401 Unauthorized
```

---

## 🔐 Segurança Implementada

### ✅ O que está protegido:

1. **Autenticação JWT**
   - Tokens com expiração (24 horas)
   - Senhas criptografadas (BCrypt)
   - Validação em cada requisição

2. **CORS Configurado**
   - Apenas origens específicas permitidas
   - Headers controlados
   - Métodos limitados

3. **Spring Security**
   - Endpoints públicos: apenas `/auth/**`
   - Todos os outros: requerem autenticação
   - Session Stateless (sem cookies de sessão)

4. **Exception Handling**
   - 401: Token inválido/expirado
   - 403: Sem permissão
   - Mensagens customizadas

### ⚠️ Para Produção:

1. **Alterar senha admin** (obrigatório!)
2. **Usar variáveis de ambiente:**
   ```properties
   spring.datasource.password=${DB_PASSWORD}
   jwt.secret=${JWT_SECRET}
   ```
3. **HTTPS obrigatório**
4. **Restringir CORS ao domínio de produção**
5. **Rate limiting**
6. **Logs de auditoria**

---

## 📋 Checklist de Verificação

Antes de testar, certifique-se:

### Backend
- [ ] PostgreSQL está rodando
- [ ] Banco `farmacia_db` existe
- [ ] Credenciais do banco estão corretas
- [ ] Backend foi reiniciado após mudanças
- [ ] Porta 8080 está livre
- [ ] Usuário admin foi criado (veja os logs)

### Frontend
- [ ] Node.js instalado
- [ ] Dependências instaladas (`npm install`)
- [ ] Arquivo `.env` configurado
- [ ] Frontend está rodando na porta 3000

### Integração
- [ ] Backend responde em http://localhost:8080
- [ ] Frontend responde em http://localhost:3000
- [ ] CORS configurado (teste com curl)
- [ ] Login funciona (teste com curl)

---

## 🎯 Teste Completo End-to-End

### 1. Iniciar Sistema

```bash
# Windows
cd DesafiosJava
iniciar-sistema.bat

# Linux/Mac
cd DesafiosJava
./iniciar-sistema.sh
```

### 2. Verificar Logs

**Backend deve mostrar:**
```
========================================
Usuário administrador criado com sucesso!
========================================
Email: admin@farmacia.com
Senha: admin123
========================================

Started DesafiosJavaApplication in X.XXX seconds
```

**Frontend deve mostrar:**
```
Compiled successfully!

You can now view app-farmacia in the browser.

  Local:            http://localhost:3000
```

### 3. Testar Login

1. Abra navegador: http://localhost:3000
2. Digite credenciais:
   - Email: `admin@farmacia.com`
   - Senha: `admin123`
3. Clique "Entrar"
4. ✅ Deve redirecionar para Dashboard

### 4. Testar Funcionalidades

1. **Dashboard** - Ver alertas
2. **Categorias** - Criar "Analgésicos"
3. **Medicamentos** - Criar "Paracetamol 500mg"
4. **Clientes** - Criar cliente teste
5. **Estoque** - Adicionar estoque ao medicamento
6. **Vendas** - Realizar uma venda teste
7. **Alertas** - Verificar alertas gerados

---

## 📈 Próximos Passos

### Desenvolvimento
1. ✅ Backend configurado e rodando
2. ✅ Frontend conectado e funcional
3. ⏭️ Testar todos os fluxos
4. ⏭️ Adicionar mais dados de teste
5. ⏭️ Personalizar conforme necessidade

### Produção
1. ⏭️ Alterar credenciais padrão
2. ⏭️ Configurar variáveis de ambiente
3. ⏭️ Configurar HTTPS
4. ⏭️ Configurar domínio em CORS
5. ⏭️ Deploy em servidor

---

## 📞 Suporte

### Documentação
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Frontend README:** `DesafioJava-farmacia-front/app-farmacia/FRONTEND_README.md`
- **Guia Rápido:** `DesafioJava-farmacia-front/app-farmacia/GUIA_RAPIDO.md`

### Logs
- **Backend:** Terminal onde rodou `mvnw spring-boot:run`
- **Frontend:** Terminal onde rodou `npm start`
- **DevTools:** F12 no navegador → Console/Network

---

## ✅ Status Final

| Componente | Status | Detalhes |
|------------|--------|----------|
| CORS | ✅ Configurado | CorsConfig.java criado |
| Security | ✅ Integrado | SecurityConfig.java atualizado |
| Usuário Admin | ✅ Automático | DataInitializer.java criado |
| Scripts | ✅ Criados | iniciar-sistema.bat/sh |
| Documentação | ✅ Completa | Este arquivo + outros |

---

**🎊 BACKEND 100% PRONTO PARA INTEGRAÇÃO COM FRONTEND! 🎊**

**Desenvolvido com ❤️ e atenção à segurança e boas práticas**
