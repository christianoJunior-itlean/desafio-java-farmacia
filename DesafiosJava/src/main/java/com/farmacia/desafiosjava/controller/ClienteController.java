package com.farmacia.desafiosjava.controller;

import com.farmacia.desafiosjava.dto.ClienteCreateResponseDTO;
import com.farmacia.desafiosjava.dto.ClienteRequestDTO;
import com.farmacia.desafiosjava.dto.ClienteResponseDTO;
import com.farmacia.desafiosjava.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @Operation(summary = "Criar cliente", 
               description = "Cadastra um novo cliente. CPF e email devem ser únicos e válidos. Permite cadastro de menores de 18 anos, mas eles não podem realizar compras (responsável deve comprar).")
    public ResponseEntity<ClienteCreateResponseDTO> criar(@Valid @RequestBody ClienteRequestDTO request) {
        ClienteCreateResponseDTO response = clienteService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO request) {
        ClienteResponseDTO response = clienteService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Lista todos os clientes cadastrados")
    public ResponseEntity<List<ClienteResponseDTO>> listar() {
        List<ClienteResponseDTO> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente", description = "Busca um cliente por ID")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }
}
