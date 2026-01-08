# 🔧 Configurações do Backend - Integração com Frontend

## ✅ Mudanças Implementadas

### 1. Configuração CORS Completa
**Arquivo:** `config/CorsConfig.java` (CRIADO)

✅ **Permite requisições de:**
- `http://localhost:3000` (React dev server)
- `http://127.0.0.1:3000` (alternativa)
- `http://localhost:3001` (porta alternativa)

✅ **Métodos HTTP permitidos:**
- GET, POST, PUT, PATCH, DELETE, OPTIONS

✅ **Headers permitidos:**
- Authorization (para JWT)
- Content-Type
- Accept
- Origin
- Access-Control-Request-Method
- Access-Control-Request-Headers

✅ **Características:**
- `allowCredentials: true` - Permite envio de cookies e headers de autorização
- `maxAge: 3600` - Cache de 1 hora para requisições preflight (OPTIONS)
- Configuração global para todas as rotas (`/**`)

### 2. SecurityConfig Atualizado
**Arquivo:** `config/SecurityConfig.java` (MODIFICADO)

✅ **Adicionado:**
- Injeção de `CorsConfigurationSource`
- Configuração `.cors()` no SecurityFilterChain
- Integração perfeita entre CORS e Spring Security

### 3. Application.properties Atualizado
**Arquivo:** `resources/application.properties` (MODIFICADO)

✅ **Adicionado seção CORS:**
```properties
spring.web.cors.allowed-origins=http://localhost:3000,http://127.0.0.1:3000
spring.web.cors.allowed-methods=GET,POST,PUT,PATCH,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

### 4. Inicializador de Dados
**Arquivo:** `config/DataInitializer.java` (CRIADO)

✅ **Cria automaticamente usuário admin:**
- **Email:** `admin@farmacia.com`
- **Senha:** `admin123`
- ⚠️ **IMPORTANTE:** Altere esta senha em produção!

✅ **Como funciona:**
- Executa ao iniciar a aplicação
- Verifica se o usuário já existe
- Só cria se não existir (não duplica)
- Exibe credenciais no console

---

## 🚀 Como Testar a Integração

### Passo 1: Reiniciar o Backend

```bash
# No diretório DesafiosJava
./mvnw clean
./mvnw spring-boot:run
```

### Passo 2: Verificar os Logs

Você deve ver no console:
```
========================================
Usuário administrador criado com sucesso!
========================================
Email: admin@farmacia.com
Senha: admin123
========================================
IMPORTANTE: Altere esta senha em produção!
========================================
```

### Passo 3: Testar a API com cURL

**Teste de Login:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@farmacia.com","senha":"admin123"}'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "username": "admin@farmacia.com"
}
```

**Teste CORS (Preflight):**
```bash
curl -X OPTIONS http://localhost:8080/medicamentos \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Authorization" \
  -v
```

**Você deve ver nos headers da resposta:**
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Credentials: true
```

### Passo 4: Testar no Frontend

1. Certifique-se que o frontend está rodando em `http://localhost:3000`
2. Acesse a página de login
3. Use as credenciais:
   - **Email:** `admin@farmacia.com`
   - **Senha:** `admin123`
4. Clique em "Entrar"
5. Você deve ser redirecionado para o Dashboard

---

## 🐛 Resolução de Problemas

### Erro: "CORS policy: No 'Access-Control-Allow-Origin' header"

**Causa:** O backend não está enviando os headers CORS corretos.

**Solução:**
1. Verifique se o backend foi reiniciado após as mudanças
2. Verifique se `CorsConfig.java` foi criado corretamente
3. Verifique os logs do backend para erros de compilação

### Erro: "401 Unauthorized" no login

**Causas possíveis:**

**1. Usuário não foi criado no banco:**
```bash
# Verifique os logs do backend ao iniciar
# Deve aparecer: "Usuário administrador criado com sucesso!"
```

**2. Senha incorreta:**
```bash
# Use exatamente: admin123
# Email: admin@farmacia.com
```

**3. Banco de dados não está rodando:**
```bash
# Verifique se o PostgreSQL está ativo
# Porta padrão: 5432
# Database: farmacia_db
```

### Erro: "Connection refused" ou "Network Error"

**Causa:** Backend não está rodando ou na porta errada.

**Solução:**
```bash
# 1. Verifique se o backend está rodando
curl http://localhost:8080/auth/login

# 2. Verifique a porta no application.properties
# Deve ser: server.port=8080

# 3. Verifique o frontend (.env)
# Deve ser: REACT_APP_API_BASE_URL=http://localhost:8080
```

### Erro: "403 Forbidden" após login

**Causa:** Token JWT não está sendo enviado corretamente.

**Solução:**
1. Verifique no DevTools (F12) → Network → Headers
2. Deve ter: `Authorization: Bearer {token}`
3. Verifique se o token foi salvo no localStorage:
   ```javascript
   // No console do navegador:
   console.log(localStorage.getItem('token'));
   ```

### Banco de dados não conecta

**Erro típico:**
```
Connection to localhost:5432 refused
```

**Solução:**
```bash
# 1. Verifique se PostgreSQL está rodando
# Windows:
services.msc
# Procure por "PostgreSQL"

# 2. Verifique as credenciais no application.properties
spring.datasource.username=postgres
spring.datasource.password=loxmeyes99  # Altere se necessário
spring.datasource.url=jdbc:postgresql://localhost:5432/farmacia_db

# 3. Crie o banco se não existir
psql -U postgres
CREATE DATABASE farmacia_db;
\q
```

---

## 🔐 Segurança - Próximos Passos

### Para Desenvolvimento
✅ As configurações atuais são adequadas

### Para Produção (IMPORTANTE!)

1. **Alterar senha do admin:**
```java
// Em DataInitializer.java
// Usar senha forte e armazená-la de forma segura
```

2. **Restringir origens CORS:**
```java
// Em CorsConfig.java
configuration.setAllowedOrigins(Arrays.asList(
    "https://seu-dominio-producao.com"
));
```

3. **Usar variáveis de ambiente:**
```properties
# application.properties
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

4. **Configurar HTTPS:**
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_PASSWORD}
```

5. **Rate Limiting:**
Implementar para evitar ataques de força bruta

---

## 📋 Checklist de Verificação

Antes de testar, certifique-se que:

- [ ] PostgreSQL está rodando
- [ ] Banco `farmacia_db` existe
- [ ] Backend foi reiniciado após as mudanças
- [ ] Backend está rodando na porta 8080
- [ ] Usuário admin foi criado (verifique os logs)
- [ ] Frontend está rodando na porta 3000
- [ ] Arquivo `.env` do frontend tem `REACT_APP_API_BASE_URL=http://localhost:8080`

---

## 🎯 Teste Completo End-to-End

### 1. Backend
```bash
cd DesafiosJava
./mvnw clean spring-boot:run
```

**Aguarde ver:**
```
Usuário administrador criado com sucesso!
Email: admin@farmacia.com
Senha: admin123
```

### 2. Frontend
```bash
cd DesafioJava-farmacia-front/app-farmacia
npm start
```

**Deve abrir:** `http://localhost:3000`

### 3. Teste de Login
1. Acesse `http://localhost:3000`
2. Digite:
   - Email: `admin@farmacia.com`
   - Senha: `admin123`
3. Clique "Entrar"
4. ✅ Deve redirecionar para Dashboard

### 4. Teste de Navegação
1. No Dashboard, clique em "Categorias"
2. Clique em "+ Nova Categoria"
3. Digite um nome: "Analgésicos"
4. Clique "Salvar"
5. ✅ Deve criar a categoria e atualizar a lista

---

## 🔄 Fluxo de Comunicação

```
Frontend (React:3000)
    ↓
1. Login → POST /auth/login
    ↓
Backend (Spring:8080)
    ↓
2. Valida credenciais
    ↓
3. Gera JWT Token
    ↓
4. Retorna { token, tipo, username }
    ↓
Frontend armazena token
    ↓
5. Requisições subsequentes → Header: Authorization: Bearer {token}
    ↓
Backend valida token via JwtAuthenticationFilter
    ↓
6. Se válido → Processa requisição
7. Se inválido → 401 Unauthorized
```

---

## 📊 Endpoints Disponíveis

### Públicos (sem autenticação)
- `POST /auth/login` - Login

### Privados (requer token JWT)
- `GET /medicamentos` - Listar medicamentos
- `GET /categorias` - Listar categorias
- `GET /clientes` - Listar clientes
- `GET /estoque/{medicamentoId}` - Consultar estoque
- `GET /vendas` - Listar vendas
- `GET /alertas/estoque-baixo` - Alertas de estoque
- `GET /alertas/validade-proxima` - Alertas de validade
- ... e todos os outros endpoints documentados no Swagger

### Documentação
- `http://localhost:8080/swagger-ui.html` - Interface Swagger
- `http://localhost:8080/v3/api-docs` - OpenAPI JSON

---

## ✅ Resumo das Mudanças

| Arquivo | Ação | Descrição |
|---------|------|-----------|
| `CorsConfig.java` | CRIADO | Configuração CORS completa |
| `SecurityConfig.java` | MODIFICADO | Integração com CORS |
| `application.properties` | MODIFICADO | Propriedades CORS adicionais |
| `DataInitializer.java` | CRIADO | Criação automática de usuário admin |

**Status:** ✅ **PRONTO PARA USO!**

---

**🎉 Backend configurado e pronto para comunicação com o frontend!**
