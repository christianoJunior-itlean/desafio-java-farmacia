package com.farmacia.desafiosjava.controller;

import com.farmacia.desafiosjava.dto.EstoqueResponseDTO;
import com.farmacia.desafiosjava.dto.MovimentacaoEstoqueRequestDTO;
import com.farmacia.desafiosjava.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Gerenciamento de estoque de medicamentos")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada", 
               description = "Registra entrada de medicamentos no estoque. Cria um novo lote automaticamente.")
    public ResponseEntity<EstoqueResponseDTO> registrarEntrada(@Valid @RequestBody MovimentacaoEstoqueRequestDTO request) {
        EstoqueResponseDTO response = estoqueService.registrarEntrada(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saida")
    @Operation(summary = "Registrar saída", 
               description = "Registra saída de medicamentos do estoque usando FIFO. Não permite saída maior que o estoque disponível.")
    public ResponseEntity<EstoqueResponseDTO> registrarSaida(@Valid @RequestBody MovimentacaoEstoqueRequestDTO request) {
        EstoqueResponseDTO response = estoqueService.registrarSaida(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medicamento/{medicamentoId}")
    @Operation(summary = "Listar estoques do medicamento",
               description = "Lista todos os lotes/entradas de estoque de um medicamento (ordenado por FIFO)")
    public ResponseEntity<List<EstoqueResponseDTO>> listarEstoques(@PathVariable Long medicamentoId) {
        List<EstoqueResponseDTO> response = estoqueService.buscarPorMedicamento(medicamentoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{medicamentoId}")
    @Operation(summary = "Consultar estoque consolidado",
               description = "Consulta o estoque total consolidado de um medicamento (soma de todos os lotes)")
    public ResponseEntity<EstoqueResponseDTO> buscarConsolidado(@PathVariable Long medicamentoId) {
        EstoqueResponseDTO response = estoqueService.buscarEstoqueConsolidado(medicamentoId);
        return ResponseEntity.ok(response);
    }
}
