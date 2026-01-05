package com.farmacia.desafiosjava.controller;

import com.farmacia.desafiosjava.dto.AlertaEstoqueBaixoDTO;
import com.farmacia.desafiosjava.dto.AlertaValidadeProximaDTO;
import com.farmacia.desafiosjava.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "Alertas de estoque baixo e medicamentos próximos do vencimento")
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping("/estoque-baixo")
    @Operation(summary = "Estoque baixo", 
               description = "Retorna medicamentos ativos com estoque abaixo do limite configurado (padrão: 10 unidades)")
    public ResponseEntity<List<AlertaEstoqueBaixoDTO>> buscarEstoqueBaixo() {
        List<AlertaEstoqueBaixoDTO> alertas = alertaService.buscarEstoqueBaixo();
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/validade-proxima")
    @Operation(summary = "Validade próxima", 
               description = "Retorna medicamentos ativos com vencimento próximo (padrão: 30 dias)")
    public ResponseEntity<List<AlertaValidadeProximaDTO>> buscarValidadeProxima() {
        List<AlertaValidadeProximaDTO> alertas = alertaService.buscarValidadeProxima();
        return ResponseEntity.ok(alertas);
    }
}
