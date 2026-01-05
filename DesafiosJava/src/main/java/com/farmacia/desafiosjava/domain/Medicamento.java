package com.farmacia.desafiosjava.domain;

import com.farmacia.desafiosjava.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicamentos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_medicamento_nome_dosagem", columnNames = {"nome_normalizado", "dosagem"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do medicamento é obrigatório")
    @Column(nullable = false)
    private String nome;

    @JsonIgnore
    @Column(name = "nome_normalizado", nullable = false)
    private String nomeNormalizado;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotBlank(message = "Dosagem é obrigatória")
    @Column(length = 100, nullable = false)
    private String dosagem;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Boolean deletado = false;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    @PreUpdate
    private void normalizarNome() {
        if (this.nome != null) {
            this.nomeNormalizado = StringUtils.normalizeString(this.nome);
        }
    }
}
