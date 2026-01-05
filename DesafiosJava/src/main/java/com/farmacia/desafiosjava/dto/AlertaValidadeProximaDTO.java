package com.farmacia.desafiosjava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaValidadeProximaDTO {

    private Long medicamentoId;
    private String medicamentoNome;
    private Integer quantidade;
    private LocalDate dataVencimento;
    private Long diasParaVencer;
}
