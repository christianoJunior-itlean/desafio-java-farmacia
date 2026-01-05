package com.farmacia.desafiosjava.repository;

import com.farmacia.desafiosjava.domain.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByMedicamentoIdOrderByDataMovimentacaoDesc(Long medicamentoId);

    void deleteByMedicamentoId(Long medicamentoId);
}
