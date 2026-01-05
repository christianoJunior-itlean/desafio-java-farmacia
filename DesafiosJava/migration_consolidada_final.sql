-- ============================================================================
-- MIGRATION CONSOLIDADA - Sistema de Farmácia
-- Data: 2026-01-02
-- Descrição: Cria todas as colunas e constraints necessárias para o sistema
-- ============================================================================

-- ============================================================================
-- 0. CRIAR FUNÇÃO DE NORMALIZAÇÃO DE STRINGS
-- ============================================================================
-- Objetivo: Remover acentos e converter para minúsculas (usado para validação de unicidade)

CREATE OR REPLACE FUNCTION normalizar_string(texto TEXT)
RETURNS TEXT AS $$
BEGIN
    RETURN LOWER(
        TRANSLATE(
            TRIM(texto),
            'áàâãäéèêëíìîïóòôõöúùûüçñÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇÑ',
            'aaaaaeeeeiiiiooooouuuucnaaaaaeeeeiiiiooooouuuucn'
        )
    );
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- 1. ADICIONAR COLUNAS nome_normalizado EM categorias E medicamentos
-- ============================================================================
-- Objetivo: Garantir unicidade de nomes independente de acentos/maiúsculas

-- Adicionar em categorias
ALTER TABLE categorias
ADD COLUMN IF NOT EXISTS nome_normalizado VARCHAR(255);

-- Atualizar dados existentes em categorias
UPDATE categorias
SET nome_normalizado = normalizar_string(nome)
WHERE nome_normalizado IS NULL OR nome_normalizado = '';

-- Tornar obrigatória e única em categorias
ALTER TABLE categorias
ALTER COLUMN nome_normalizado SET NOT NULL;

-- Remover constraints antigas de categorias
DO $$
DECLARE
    constraint_rec RECORD;
BEGIN
    FOR constraint_rec IN
        SELECT tc.constraint_name
        FROM information_schema.table_constraints tc
        WHERE tc.table_name = 'categorias'
        AND tc.constraint_type = 'UNIQUE'
        AND tc.constraint_name != 'uk_categorias_nome_normalizado'
    LOOP
        EXECUTE 'ALTER TABLE categorias DROP CONSTRAINT IF EXISTS ' || constraint_rec.constraint_name;
        RAISE NOTICE 'Constraint removida de categorias: %', constraint_rec.constraint_name;
    END LOOP;
END $$;

ALTER TABLE categorias
DROP CONSTRAINT IF EXISTS uk_categorias_nome_normalizado;

ALTER TABLE categorias
ADD CONSTRAINT uk_categorias_nome_normalizado UNIQUE (nome_normalizado);

-- Adicionar em medicamentos
ALTER TABLE medicamentos
ADD COLUMN IF NOT EXISTS nome_normalizado VARCHAR(255);

-- Atualizar dados existentes em medicamentos
UPDATE medicamentos
SET nome_normalizado = normalizar_string(nome)
WHERE nome_normalizado IS NULL OR nome_normalizado = '';

-- Tornar obrigatória em medicamentos
ALTER TABLE medicamentos
ALTER COLUMN nome_normalizado SET NOT NULL;

COMMENT ON COLUMN categorias.nome_normalizado IS 'Nome normalizado (sem acentos, minúsculas) para validação de unicidade';
COMMENT ON COLUMN medicamentos.nome_normalizado IS 'Nome normalizado (sem acentos, minúsculas) para validação de unicidade com dosagem';

-- ============================================================================
-- 2. ADICIONAR COLUNA 'deletado' NA TABELA medicamentos
-- ============================================================================
-- Objetivo: Controlar soft delete permanente de medicamentos vendidos

ALTER TABLE medicamentos
ADD COLUMN IF NOT EXISTS deletado BOOLEAN NOT NULL DEFAULT FALSE;

-- Atualizar medicamentos inativos que já foram vendidos para deletado=true
UPDATE medicamentos m
SET deletado = TRUE
WHERE ativo = FALSE
AND EXISTS (
    SELECT 1 FROM itens_venda iv WHERE iv.medicamento_id = m.id
);

COMMENT ON COLUMN medicamentos.deletado IS 'Indica se medicamento foi inativado permanentemente (soft delete). Não pode ser reativado.';

-- ============================================================================
-- 3. ADICIONAR COLUNA 'nome_responsavel' NA TABELA clientes
-- ============================================================================
-- Objetivo: Armazenar nome do responsável legal para clientes menores de 18 anos

ALTER TABLE clientes
ADD COLUMN IF NOT EXISTS nome_responsavel VARCHAR(255);

COMMENT ON COLUMN clientes.nome_responsavel IS 'Nome do responsável legal (obrigatório para menores de 18 anos)';

-- ============================================================================
-- 4. ATUALIZAR CONSTRAINT DE UNICIDADE DE MEDICAMENTOS
-- ============================================================================
-- Objetivo: Permitir mesmo medicamento com dosagens diferentes (ex: Paracetamol 500mg e 750mg)

-- Tornar dosagem obrigatória (preencher valores NULL primeiro)
UPDATE medicamentos
SET dosagem = 'NÃO ESPECIFICADA'
WHERE dosagem IS NULL OR dosagem = '';

ALTER TABLE medicamentos
ALTER COLUMN dosagem SET NOT NULL;

-- Remover constraint única antiga (apenas nome_normalizado)
DO $$
DECLARE
    constraint_rec RECORD;
BEGIN
    FOR constraint_rec IN
        SELECT tc.constraint_name
        FROM information_schema.table_constraints tc
        WHERE tc.table_name = 'medicamentos'
        AND tc.constraint_type = 'UNIQUE'
        AND tc.constraint_name != 'uk_medicamento_nome_dosagem'
        AND tc.constraint_name LIKE '%nome%'
    LOOP
        EXECUTE 'ALTER TABLE medicamentos DROP CONSTRAINT IF EXISTS ' || constraint_rec.constraint_name;
        RAISE NOTICE 'Constraint removida: %', constraint_rec.constraint_name;
    END LOOP;
END $$;

-- Criar nova constraint única composta (nome_normalizado + dosagem)
ALTER TABLE medicamentos
DROP CONSTRAINT IF EXISTS uk_medicamento_nome_dosagem;

ALTER TABLE medicamentos
ADD CONSTRAINT uk_medicamento_nome_dosagem UNIQUE (nome_normalizado, dosagem);

COMMENT ON CONSTRAINT uk_medicamento_nome_dosagem ON medicamentos IS
    'Garante unicidade de medicamento por nome e dosagem. Permite Paracetamol 500mg e Paracetamol 750mg como produtos diferentes.';

-- ============================================================================
-- VERIFICAÇÕES FINAIS
-- ============================================================================

-- Verificar estrutura da tabela medicamentos
SELECT
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'medicamentos'
AND column_name IN ('ativo', 'deletado', 'dosagem', 'nome_normalizado')
ORDER BY column_name;

-- Verificar constraints UNIQUE da tabela medicamentos
SELECT
    tc.constraint_name,
    STRING_AGG(ccu.column_name, ', ' ORDER BY ccu.column_name) as columns
FROM information_schema.table_constraints tc
JOIN information_schema.constraint_column_usage ccu
    ON tc.constraint_name = ccu.constraint_name
WHERE tc.table_name = 'medicamentos'
    AND tc.constraint_type = 'UNIQUE'
GROUP BY tc.constraint_name;

-- Verificar estrutura da tabela clientes
SELECT
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'clientes'
AND column_name = 'nome_responsavel';

-- Verificar medicamentos com soft delete
SELECT
    id,
    nome,
    dosagem,
    ativo,
    deletado,
    CASE
        WHEN deletado = TRUE THEN 'Soft Delete Permanente (já foi vendido)'
        WHEN ativo = FALSE THEN 'Inativo Temporário (pode ser reativado)'
        ELSE 'Ativo'
    END as status
FROM medicamentos
ORDER BY deletado DESC, ativo, nome
LIMIT 10;

-- Verificar clientes menores de 18 anos
SELECT
    id,
    nome_completo,
    data_nascimento,
    DATE_PART('year', AGE(data_nascimento)) as idade,
    nome_responsavel,
    CASE
        WHEN DATE_PART('year', AGE(data_nascimento)) < 18 THEN
            CASE
                WHEN nome_responsavel IS NOT NULL THEN 'OK - Menor com responsável'
                ELSE '⚠️ ATENÇÃO - Menor sem responsável'
            END
        ELSE 'Maior de idade'
    END as status
FROM clientes
ORDER BY data_nascimento DESC
LIMIT 10;

-- ============================================================================
-- RESUMO DAS MUDANÇAS
-- ============================================================================
-- ✅ Função normalizar_string() - Remove acentos e converte para minúsculas
-- ✅ categorias.nome_normalizado - Garante unicidade independente de acentos
-- ✅ medicamentos.nome_normalizado - Usado na constraint composta com dosagem
-- ✅ medicamentos.deletado - Controla soft delete permanente
-- ✅ medicamentos.dosagem - Agora obrigatória (NOT NULL)
-- ✅ medicamentos constraint - UNIQUE(nome_normalizado, dosagem) permite dosagens diferentes
-- ✅ clientes.nome_responsavel - Responsável legal para menores de 18 anos
-- ============================================================================

-- ============================================================================
-- INSTRUÇÕES DE USO
-- ============================================================================
-- 1. Execute este script APÓS criar o banco de dados e ANTES de iniciar a aplicação
-- 2. O Hibernate (ddl-auto=update) irá criar as tabelas base
-- 3. Este script adiciona as colunas e constraints necessárias
-- 4. Todos os comandos são idempotentes (podem ser executados múltiplas vezes)
-- ============================================================================

-- ============================================================================
-- FINALIZAÇÃO
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================================================';
    RAISE NOTICE 'Migration executada com sucesso!';
    RAISE NOTICE '============================================================================';
    RAISE NOTICE 'Próximos passos:';
    RAISE NOTICE '1. Inicie a aplicação Spring Boot';
    RAISE NOTICE '2. Acesse o Swagger em http://localhost:8080/swagger-ui.html';
    RAISE NOTICE '3. Registre um usuário em /auth/registrar';
    RAISE NOTICE '4. Faça login em /auth/login para obter o token JWT';
    RAISE NOTICE '============================================================================';
END $$;

