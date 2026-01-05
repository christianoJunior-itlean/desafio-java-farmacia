package com.farmacia.desafiosjava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaResponseDTO {

    private Long id;
    private ClienteResponseDTO cliente;
    private LocalDateTime dataHora;
    private BigDecimal valorTotal;
    private List<ItemVendaResponseDTO> itens;
}
