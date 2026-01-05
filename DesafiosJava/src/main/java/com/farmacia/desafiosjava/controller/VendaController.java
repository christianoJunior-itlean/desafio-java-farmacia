package com.farmacia.desafiosjava.controller;

import com.farmacia.desafiosjava.dto.VendaRequestDTO;
import com.farmacia.desafiosjava.dto.VendaResponseDTO;
import com.farmacia.desafiosjava.service.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
@Tag(name = "Vendas", description = "Gerenciamento de vendas")
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    @Operation(summary = "Criar venda", 
               description = "Registra uma nova venda. Valida estoque, medicamentos ativos/vencidos e idade do cliente (18+). Atualiza estoque automaticamente.")
    public ResponseEntity<VendaResponseDTO> criar(@Valid @RequestBody VendaRequestDTO request) {
        VendaResponseDTO response = vendaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar vendas", description = "Lista todas as vendas realizadas")
    public ResponseEntity<List<VendaResponseDTO>> listar() {
        List<VendaResponseDTO> vendas = vendaService.listarTodas();
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venda", description = "Busca uma venda por ID")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        VendaResponseDTO venda = vendaService.buscarPorId(id);
        return ResponseEntity.ok(venda);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar vendas por cliente", description = "Lista todas as vendas de um cliente específico")
    public ResponseEntity<List<VendaResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<VendaResponseDTO> vendas = vendaService.listarPorCliente(clienteId);
        return ResponseEntity.ok(vendas);
    }
}
