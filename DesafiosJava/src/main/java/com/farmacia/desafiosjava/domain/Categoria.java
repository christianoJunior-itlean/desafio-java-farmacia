package com.farmacia.desafiosjava.domain;

import com.farmacia.desafiosjava.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da categoria é obrigatório")
    @Column(nullable = false)
    private String nome;

    @JsonIgnore
    @Column(name = "nome_normalizado", unique = true, nullable = false)
    private String nomeNormalizado;

    @PrePersist
    @PreUpdate
    private void normalizarNome() {
        if (this.nome != null) {
            this.nomeNormalizado = StringUtils.normalizeString(this.nome);
        }
    }
}
