# 🏥 Sistema de Gerenciamento de Farmácia

API REST completa para gerenciamento de farmácia, desenvolvida com Spring Boot 3.5.9 e Java 25 LTS.

## 📋 Sobre o Projeto

Sistema completo de gerenciamento de farmácia com controle de:
- **Medicamentos e Categorias** - Com suporte a dosagens diferentes e normalização de nomes
- **Estoque e Movimentações** - Sistema FIFO automático com controle de lotes
- **Clientes** - Com validação de idade e responsável legal para menores
- **Vendas** - Com validação completa de estoque, vencimento e regras de negócio
- **Alertas** - Estoque baixo e validade próxima em tempo real
- **Autenticação JWT** - Sistema seguro de autenticação stateless

### 🎯 Destaques e Melhorias Implementadas

- ✨ **Normalização de nomes**: case-insensitive, sem acentos (Ex: "Analgésicos" = "analgesicos")
- 💊 **Dosagens diferentes**: Paracetamol 500mg e Paracetamol 750mg como produtos distintos
- 🗑️ **Soft Delete Inteligente**: Medicamentos vendidos são inativados permanentemente, outros podem ser deletados
- 👶 **Responsável Legal**: Campo obrigatório para clientes menores de 18 anos (conformidade LGPD)
- 📦 **Sistema FIFO**: Controle automático de lotes por vencimento
- ⚠️ **Validações Robustas**: Impede venda de medicamentos vencidos, inativos ou deletados
- 💬 **Mensagens Amigáveis**: Respostas claras em português para todas as operações

## 🚀 Tecnologias Utilizadas

- **Java 25 LTS**
- **Spring Boot 3.5.9**
- **Spring Web** - API REST
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **JWT (JSON Web Token)** - Autenticação stateless
- **Bean Validation** - Validação de dados
- **PostgreSQL** - Banco de dados relacional
- **OpenAPI/Swagger** - Documentação da API
- **Lombok** - Redução de boilerplate
- **Maven** - Gerenciamento de dependências

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas bem definida:

```
src/main/java/com/farmacia/desafiosjava/
├── config/          # Configurações (Security, JWT, Swagger)
├── controller/      # Controllers REST
├── service/         # Regras de negócio
├── repository/      # Acesso a dados (JPA)
├── domain/          # Entidades do domínio
├── dto/             # Data Transfer Objects
└── exception/       # Tratamento de exceções
```

## 📦 Entidades do Sistema

### Medicamento
- Nome único e obrigatório (normalizado: insensível a acentos/maiúsculas)
- **Dosagem obrigatória** - Permite diferentes dosagens do mesmo medicamento
- Preço maior que zero
- Status ativo/inativo
- **Campo deletado** - Controla soft delete permanente (medicamentos vendidos)
- Categoria
- Data de criação

### Categoria
- Nome único e obrigatório (normalizado: insensível a acentos/maiúsculas)
- Não pode ser excluída se houver medicamentos vinculados

### Estoque
- Controle por lotes individuais (cada entrada = um lote)
- Sistema FIFO (First In, First Out) automático
- **Data de vencimento por lote** (obrigatória)
- Quantidade disponível por lote
- Cada entrada tem número de lote único
- Validação: não permite venda de medicamentos vencidos

**⚠️ IMPORTANTE:** Sistema simplificado! A tabela `estoque` funciona diretamente como controle de lotes.
Não existe tabela `lotes` separada - cada registro de estoque é um lote individual.

### Cliente
- CPF único e válido (formato: XXX.XXX.XXX-XX)
- Email único e válido
- Data de nascimento
- **Nome do responsável legal** (obrigatório para menores de 18 anos)
- Deve ter 18+ anos para realizar compras (menores podem ser cadastrados mas não compram)

### Venda
- Vinculada a um cliente (validação de idade 18+)
- Contém itens de venda
- Valor total calculado automaticamente
- Data e hora do registro
- Baixa automática de estoque usando FIFO
- Validações: medicamento ativo, não vencido, estoque suficiente

## ⚙️ Configuração e Execução

### Pré-requisitos

- Java 25 LTS instalado
- PostgreSQL 12+ instalado e rodando
- Maven 3.x instalado (ou usar o wrapper mvnw incluído)

### 1. Configurar o Banco de Dados

Crie um banco de dados PostgreSQL:

```sql
CREATE DATABASE farmacia_db;
```

**Execute a migration consolidada** (necessária para criar todas as colunas e constraints):

```bash
psql -U postgres -d farmacia_db -f migration_consolidada_final.sql
```

Ou execute manualmente via pgAdmin/DBeaver abrindo o arquivo `migration_consolidada_final.sql`.

### 2. Configurar Credenciais

Edite o arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/farmacia_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 3. Executar a Aplicação

**Windows:**
```bash
mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 📚 Documentação da API (Swagger)

Acesse a documentação interativa da API:

**Swagger UI:** http://localhost:8080/swagger-ui.html

**OpenAPI JSON:** http://localhost:8080/v3/api-docs

## 🔐 Autenticação

A API utiliza JWT (JSON Web Token) para autenticação.

### 1. Registrar um Usuário

```bash
POST /auth/registrar
Content-Type: application/json

{
  "username": "admin",
  "senha": "senha123"
}
```

### 2. Fazer Login

```bash
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "senha": "senha123"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "username": "admin"
}
```

### 3. Usar o Token

Inclua o token no header de todas as requisições:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📋 Endpoints Principais

### Categorias

```bash
POST   /categorias           # Criar categoria
GET    /categorias           # Listar todas
GET    /categorias/{id}      # Buscar por ID
PUT    /categorias/{id}      # Atualizar categoria
DELETE /categorias/{id}      # Deletar categoria
```

### Medicamentos

```bash
POST   /medicamentos                      # Criar medicamento
PUT    /medicamentos/{id}                 # Atualizar medicamento
GET    /medicamentos                      # Listar todos
GET    /medicamentos/{id}                 # Buscar por ID
GET    /medicamentos/categoria/{categoriaId}  # Listar por categoria
DELETE /medicamentos/{id}                 # Deletar (soft delete inteligente)
PATCH  /medicamentos/{id}/status          # Alterar status (ativar/inativar)
```

### Clientes

```bash
POST   /clientes             # Criar cliente
PUT    /clientes/{id}        # Atualizar cliente
GET    /clientes             # Listar todos
GET    /clientes/{id}        # Buscar por ID
```

### Estoque

```bash
POST   /estoque/entrada              # Registrar entrada (cria lote automaticamente)
POST   /estoque/saida                # Registrar saída (usa FIFO)
GET    /estoque/{medicamentoId}      # Consultar estoque consolidado (total)
GET    /estoque/medicamento/{medicamentoId}  # Listar todos os lotes do medicamento
```

### Vendas

```bash
POST   /vendas               # Criar venda
GET    /vendas               # Listar todas
GET    /vendas/{id}          # Buscar por ID
GET    /vendas/cliente/{clienteId}  # Listar por cliente
```

### Alertas

```bash
GET    /alertas/estoque-baixo      # Medicamentos com estoque baixo
GET    /alertas/validade-proxima   # Medicamentos próximos do vencimento
```

## 💡 Exemplos de Uso

### Criar uma Categoria

```bash
POST /categorias
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Analgésicos"
}
```

### Criar um Medicamento

```bash
POST /medicamentos
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Paracetamol",
  "dosagem": "500mg",
  "descricao": "Analgésico e antipirético",
  "preco": 15.90,
  "ativo": true,
  "categoriaId": 1
}
```

**Nota:** A dosagem é obrigatória. Isso permite cadastrar o mesmo medicamento com dosagens diferentes:
- Paracetamol 500mg
- Paracetamol 750mg
- Dipirona 500mg
- Dipirona 1g

### Registrar Entrada no Estoque

```bash
POST /estoque/entrada
Authorization: Bearer {token}
Content-Type: application/json

{
  "medicamentoId": 1,
  "quantidade": 100,
  "dataVencimento": "2026-12-31",
  "observacao": "LOTE-2026-001"
}
```

**Nota:** A `dataVencimento` é obrigatória e deve ser futura. Cada entrada cria um lote único no estoque.

### Criar um Cliente

```bash
POST /clientes
Authorization: Bearer {token}
Content-Type: application/json

{
  "nomeCompleto": "João Silva",
  "cpf": "123.456.789-00",
  "email": "joao@email.com",
  "dataNascimento": "1990-05-15"
}
```

**Para cliente menor de 18 anos** (responsável é obrigatório):

```bash
POST /clientes
Authorization: Bearer {token}
Content-Type: application/json

{
  "nomeCompleto": "Maria Silva",
  "cpf": "987.654.321-00",
  "email": "maria@email.com",
  "dataNascimento": "2010-03-20",
  "nomeResponsavel": "Ana Silva (mãe)"
}
```

**Resposta para menor:**
```json
{
  "id": 2,
  "nomeCompleto": "Maria Silva",
  "idade": 16,
  "podeComprar": false,
  "mensagem": "Cliente cadastrado com sucesso! ⚠️ ATENÇÃO: Cliente menor de 18 anos (16 anos). Responsável legal cadastrado: Ana Silva (mãe). Este cliente NÃO pode realizar compras diretamente..."
}
```

### Realizar uma Venda

```bash
POST /vendas
Authorization: Bearer {token}
Content-Type: application/json

{
  "clienteId": 1,
  "itens": [
    {
      "medicamentoId": 1,
      "quantidade": 2
    },
    {
      "medicamentoId": 2,
      "quantidade": 1
    }
  ]
}
```

## 🔍 Regras de Negócio Implementadas

### Medicamentos
- ✅ Nome único (normalizado - insensível a acentos/maiúsculas)
- ✅ Dosagem obrigatória - permite dosagens diferentes do mesmo medicamento
- ✅ Preço deve ser maior que zero
- ✅ Medicamento inativo não pode ser vendido
- ✅ **Soft Delete Inteligente:**
  - Se já foi vendido: soft delete permanente (deletado=true, não pode ser reativado)
  - Se nunca foi vendido: delete físico (remove do banco)
- ✅ Medicamentos deletados (soft delete) não aparecem em alertas

### Categorias
- ✅ Nome único (normalizado - insensível a acentos/maiúsculas)
- ✅ Não permite exclusão se houver medicamentos vinculados

### Clientes
- ✅ CPF obrigatório, válido e único
- ✅ Email obrigatório, válido e único
- ✅ Permite cadastro de menores de 18 anos
- ✅ **Nome do responsável legal obrigatório para menores**
- ✅ Mensagem informativa no cadastro de menores
- ✅ Deve ter 18 anos ou mais para comprar (validado na venda)

### Estoque
- ✅ Data de vencimento obrigatória e deve ser futura
- ✅ Entrada aumenta estoque (cria novo lote)
- ✅ Saída diminui estoque usando **FIFO** (primeiro que vence, primeiro que sai)
- ✅ Não permite saída maior que disponível
- ✅ Não permite venda de medicamentos vencidos
- ✅ Registra todas as movimentações com data, tipo e quantidade
- ✅ Número de lote único por medicamento

### Vendas
- ✅ Venda deve ter ao menos um item
- ✅ Preço unitário = preço atual do medicamento (calculado automaticamente)
- ✅ Não permite venda de medicamento inativo
- ✅ Não permite venda de medicamento deletado (soft delete)
- ✅ Não permite venda de medicamento vencido
- ✅ Valida estoque disponível (considera apenas não vencidos)
- ✅ Atualiza estoque automaticamente usando FIFO
- ✅ Calcula valor total no backend
- ✅ Valida idade mínima do cliente (18+)
- ✅ Registra data e hora da venda

### Alertas
- ✅ Estoque baixo: quantidade < 10 (configurável)
- ✅ Validade próxima: vencimento nos próximos 30 dias (configurável)
- ✅ Apenas medicamentos **ativos** e **não deletados**
- ✅ Apenas lotes com quantidade disponível > 0

## 🔧 Configurações

Edite `application.properties` para ajustar:

```properties
# Limite para alerta de estoque baixo
estoque.limite-baixo=10

# Dias de antecedência para alerta de vencimento
validade.dias-alerta=30

# Tempo de expiração do token JWT (em milissegundos)
jwt.expiration=86400000
```

## 🛡️ Segurança

- Todos os endpoints (exceto `/auth/**` e Swagger) requerem autenticação
- Senhas são criptografadas com BCrypt
- Tokens JWT com expiração configurável
- CORS habilitado para desenvolvimento

## 📊 Tratamento de Erros

A API retorna erros estruturados:

```json
{
  "timestamp": "2025-12-26T10:30:00",
  "status": 400,
  "error": "Business Rule Violation",
  "message": "Estoque insuficiente",
  "path": "/vendas",
  "details": []
}
```

## 🧪 Testando a API

### Com cURL

```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","senha":"senha123"}'

# Criar medicamento
curl -X POST http://localhost:8080/medicamentos \
  -H "Authorization: Bearer {seu_token}" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Aspirina","preco":10.50,"ativo":true}'
```

### Com Swagger UI

1. Acesse http://localhost:8080/swagger-ui.html
2. Clique em "Authorize" (cadeado no topo)
3. Insira: `Bearer {seu_token}`
4. Teste os endpoints diretamente

## 📌 Melhorias e Funcionalidades Avançadas

### ✅ Normalização de Nomes (Case-Insensitive + Sem Acentos)

Categorias e medicamentos usam normalização de nomes para garantir unicidade independente de acentos ou maiúsculas/minúsculas.

**Exemplos:**
- "Analgésicos" = "ANALGESICOS" = "analgesicos" → Tratados como mesmo nome
- "Paracetamol" = "PARACETAMOL" = "paracetamól" → Tratados como mesmo nome

**Como funciona:**
- Função PostgreSQL `normalizar_string()` criada automaticamente
- Campo `nome_normalizado` em categorias e medicamentos
- Validação automática via `@PrePersist` e `@PreUpdate`

### ✅ Medicamentos com Dosagens Diferentes

O mesmo medicamento pode ter múltiplas dosagens cadastradas como produtos distintos.

**Constraint:** `UNIQUE(nome_normalizado, dosagem)`

**Exemplos permitidos:**
- Paracetamol 500mg (produto 1)
- Paracetamol 750mg (produto 2)
- Dipirona 500mg (produto 3)
- Dipirona 1g (produto 4)

**Bloqueado:**
- Paracetamol 500mg (duplicado)

### ✅ Soft Delete Inteligente

Medicamentos têm comportamento de exclusão inteligente:

**Se já foi vendido:**
- Soft delete permanente
- `ativo = false` + `deletado = true`
- Não pode ser reativado
- Não aparece em alertas
- Não pode ser vendido

**Se nunca foi vendido:**
- Delete físico (remove do banco)
- Liberado completamente

### ✅ Responsável Legal para Menores (LGPD)

Clientes menores de 18 anos podem ser cadastrados, mas:
- Campo `nomeResponsavel` é **obrigatório**
- Mensagem informativa ao cadastrar
- Não podem realizar compras (validação na venda)
- Conformidade com LGPD

### ✅ Sistema FIFO Completo

O estoque usa FIFO (First In, First Out) automático:
- Cada entrada = um lote único
- Ordenação por data de vencimento
- Baixa automática do lote mais próximo de vencer
- Não permite venda de lotes vencidos
- Rastreabilidade completa por lote

### ✅ Mensagens Amigáveis

Todas as operações retornam mensagens claras em português:
- Cadastros bem-sucedidos com informações relevantes
- Erros descritivos e acionáveis
- Validações com orientações claras
- Status codes apropriados (200, 201, 400, 404, 500)

## 📊 Estatísticas do Projeto

- **28 Endpoints REST** implementados
- **8 Entidades JPA** no domínio
- **100% dos requisitos** da estória do usuário atendidos
- **7 Melhorias significativas** além dos requisitos
- **Arquitetura em camadas** bem definida
- **Tratamento robusto de erros** com mensagens amigáveis
- **Documentação Swagger** completa e interativa
- **Autenticação JWT** segura

## 🎯 Conformidade com Requisitos

Este projeto atende **100% dos requisitos funcionais e técnicos** da estória do usuário:

✅ Todos os 22 endpoints solicitados implementados  
✅ Todas as validações e regras de negócio implementadas  
✅ Autenticação JWT completa  
✅ Documentação Swagger completa  
✅ Tratamento de erros robusto  
✅ Organização em camadas (Controller → Service → Repository)  
✅ Java 25 LTS e Spring Boot 3.x  
✅ PostgreSQL como banco de dados  
✅ Bean Validation para validações  

**➕ Bônus:** 6 endpoints extras e múltiplas melhorias significativas!

## 📝 Licença

Este projeto está sob a licença MIT.

## 👨‍💻 Desenvolvedor

Desenvolvido com 💙 seguindo as melhores práticas de desenvolvimento Java e Spring Boot.

**Características Técnicas:**
- Clean Code e SOLID principles
- RESTful API design
- Transaction management
- Error handling strategy
- Security best practices
- Database normalization
- Business rules validation

---

**Status:** ✅ Projeto completo e pronto para produção!

Para dúvidas ou sugestões, entre em contato através do repositório.
