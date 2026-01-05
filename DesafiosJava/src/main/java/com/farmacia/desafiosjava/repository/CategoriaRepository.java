package com.farmacia.desafiosjava.repository;

import com.farmacia.desafiosjava.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNome(String nome);

    Optional<Categoria> findByNomeNormalizado(String nomeNormalizado);

    boolean existsByNome(String nome);

    boolean existsByNomeNormalizado(String nomeNormalizado);

    boolean existsByNomeNormalizadoAndIdNot(String nomeNormalizado, Long id);
}
