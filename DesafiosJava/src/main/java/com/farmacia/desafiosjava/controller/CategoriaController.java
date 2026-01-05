package com.farmacia.desafiosjava.controller;

import com.farmacia.desafiosjava.dto.CategoriaRequestDTO;
import com.farmacia.desafiosjava.dto.CategoriaResponseDTO;
import com.farmacia.desafiosjava.dto.MessageResponseDTO;
import com.farmacia.desafiosjava.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Gerenciamento de categorias de medicamentos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @Operation(summary = "Criar categoria", description = "Cria uma nova categoria de medicamento")
    public ResponseEntity<CategoriaResponseDTO> criar(@Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaResponseDTO response = categoriaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar categorias", description = "Lista todas as categorias cadastradas")
    public ResponseEntity<List<CategoriaResponseDTO>> listar() {
        List<CategoriaResponseDTO> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria", description = "Busca uma categoria por ID")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        CategoriaResponseDTO categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaResponseDTO response = categoriaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar categoria", description = "Deleta uma categoria (não permite se houver medicamentos vinculados)")
    public ResponseEntity<MessageResponseDTO> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.ok(new MessageResponseDTO("Categoria deletada com sucesso"));
    }
}
