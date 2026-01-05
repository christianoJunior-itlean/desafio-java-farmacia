package com.farmacia.desafiosjava.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "estoque", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"medicamento_id", "numero_lote"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Número do lote é obrigatório")
    @Column(name = "numero_lote", nullable = false)
    private String numeroLote;

    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    @NotNull(message = "Medicamento é obrigatório")
    private Medicamento medicamento;

    @Min(value = 0, message = "Quantidade não pode ser negativa")
    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel = 0;

    @NotNull(message = "Data de vencimento é obrigatória")
    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
}
