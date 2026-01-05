package com.farmacia.desafiosjava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueResponseDTO {

    private Long id;
    private Long medicamentoId;
    private String medicamentoNome;
    private Integer quantidadeAtual;
    private LocalDate dataVencimento;
}
