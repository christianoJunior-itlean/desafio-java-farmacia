package com.farmacia.desafiosjava.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoqueRequestDTO {

    @NotNull(message = "ID do medicamento é obrigatório")
    private Long medicamentoId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantidade;

    @Schema(example = "2026-12-31")
    @NotNull(message = "Data de vencimento é obrigatória")
    private LocalDate dataVencimento;

    @Schema(example = "LOTE-2025-001", description = "Número do lote (opcional). Se não informado, será gerado automaticamente.")
    private String observacao;
}
