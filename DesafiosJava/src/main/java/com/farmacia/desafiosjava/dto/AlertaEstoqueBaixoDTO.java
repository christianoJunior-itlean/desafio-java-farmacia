package com.farmacia.desafiosjava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaEstoqueBaixoDTO {

    private Long medicamentoId;
    private String medicamentoNome;
    private Integer quantidadeAtual;
    private Integer limiteBaixo;
    private BigDecimal preco;
}
