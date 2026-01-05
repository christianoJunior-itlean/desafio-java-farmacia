package com.farmacia.desafiosjava.repository;

import com.farmacia.desafiosjava.domain.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    Optional<Medicamento> findByNome(String nome);

    Optional<Medicamento> findByNomeNormalizado(String nomeNormalizado);

    Optional<Medicamento> findByNomeNormalizadoAndDosagem(String nomeNormalizado, String dosagem);

    boolean existsByNome(String nome);

    boolean existsByNomeNormalizado(String nomeNormalizado);

    boolean existsByNomeNormalizadoAndDosagem(String nomeNormalizado, String dosagem);

    boolean existsByNomeNormalizadoAndIdNot(String nomeNormalizado, Long id);

    boolean existsByNomeNormalizadoAndDosagemAndIdNot(String nomeNormalizado, String dosagem, Long id);

    List<Medicamento> findByAtivoTrue();

    @Query("SELECT m FROM Medicamento m WHERE m.categoria.id = :categoriaId")
    List<Medicamento> findByCategoriaId(Long categoriaId);

    @Query("SELECT COUNT(m) > 0 FROM Medicamento m JOIN ItemVenda iv ON iv.medicamento.id = m.id WHERE m.id = :medicamentoId")
    boolean foiVendido(Long medicamentoId);

    @Query("SELECT COUNT(m) > 0 FROM Medicamento m JOIN MovimentacaoEstoque me ON me.medicamento.id = m.id WHERE m.id = :medicamentoId")
    boolean temMovimentacoes(Long medicamentoId);
}
