package com.farmacia.desafiosjava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private String dosagem;
    private BigDecimal preco;
    private Boolean ativo;
    private CategoriaResponseDTO categoria;
    private LocalDateTime criadoEm;
}
