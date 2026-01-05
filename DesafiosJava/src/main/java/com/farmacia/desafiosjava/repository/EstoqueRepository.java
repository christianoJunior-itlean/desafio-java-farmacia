package com.farmacia.desafiosjava.repository;

import com.farmacia.desafiosjava.domain.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    // Buscar entradas de estoque (lotes) de um medicamento ordenados por data de vencimento (FIFO)
    @Query("SELECT e FROM Estoque e WHERE e.medicamento.id = :medicamentoId AND e.ativo = true AND e.quantidadeDisponivel > 0 ORDER BY e.dataVencimento ASC, e.criadoEm ASC")
    List<Estoque> findEstoquesDisponiveis(Long medicamentoId);

    // Buscar todos os estoques de um medicamento (incluindo zerados)
    @Query("SELECT e FROM Estoque e WHERE e.medicamento.id = :medicamentoId ORDER BY e.dataVencimento ASC")
    List<Estoque> findAllByMedicamentoId(Long medicamentoId);

    // Buscar entrada específica por número de lote e medicamento
    Optional<Estoque> findByNumeroLoteAndMedicamentoId(String numeroLote, Long medicamentoId);

    // Buscar entradas vencidas
    @Query("SELECT e FROM Estoque e WHERE e.medicamento.id = :medicamentoId AND e.dataVencimento < :dataAtual AND e.quantidadeDisponivel > 0")
    List<Estoque> findEstoquesVencidos(Long medicamentoId, LocalDate dataAtual);

    // Buscar entradas com vencimento próximo (para alertas)
    @Query("SELECT e FROM Estoque e WHERE e.medicamento.ativo = true AND e.medicamento.deletado = false AND e.ativo = true AND e.quantidadeDisponivel > 0 AND e.dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY e.dataVencimento ASC")
    List<Estoque> findEstoquesComVencimentoProximo(LocalDate dataInicio, LocalDate dataFim);

    // Buscar entradas com estoque baixo (soma das quantidades < limite)
    @Query("SELECT e FROM Estoque e WHERE e.medicamento.ativo = true AND e.medicamento.deletado = false AND e.ativo = true AND e.quantidadeDisponivel > 0 GROUP BY e.medicamento HAVING SUM(e.quantidadeDisponivel) < :limite")
    List<Estoque> findEstoqueBaixo(Integer limite);

    // Calcular quantidade total disponível de um medicamento
    @Query("SELECT COALESCE(SUM(e.quantidadeDisponivel), 0) FROM Estoque e WHERE e.medicamento.id = :medicamentoId AND e.ativo = true AND e.quantidadeDisponivel > 0")
    Integer calcularQuantidadeTotal(Long medicamentoId);

    // Calcular quantidade total disponível de um medicamento (apenas entradas não vencidas)
    @Query("SELECT COALESCE(SUM(e.quantidadeDisponivel), 0) FROM Estoque e WHERE e.medicamento.id = :medicamentoId AND e.ativo = true AND e.quantidadeDisponivel > 0 AND e.dataVencimento >= :dataAtual")
    Integer calcularQuantidadeTotalNaoVencida(Long medicamentoId, LocalDate dataAtual);

    // Deletar todos os estoques de um medicamento
    void deleteByMedicamentoId(Long medicamentoId);
}
