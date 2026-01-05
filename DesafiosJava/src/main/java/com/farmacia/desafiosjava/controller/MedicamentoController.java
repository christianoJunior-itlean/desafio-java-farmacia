package com.farmacia.desafiosjava.controller;

import com.farmacia.desafiosjava.dto.AlterarStatusDTO;
import com.farmacia.desafiosjava.dto.MedicamentoRequestDTO;
import com.farmacia.desafiosjava.dto.MedicamentoResponseDTO;
import com.farmacia.desafiosjava.dto.MessageResponseDTO;
import com.farmacia.desafiosjava.service.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicamentos")
@RequiredArgsConstructor
@Tag(name = "Medicamentos", description = "Gerenciamento de medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    @PostMapping
    @Operation(summary = "Criar medicamento", 
               description = "Cria um novo medicamento. Nome deve ser único e preço maior que zero.")
    public ResponseEntity<MedicamentoResponseDTO> criar(@Valid @RequestBody MedicamentoRequestDTO request) {
        MedicamentoResponseDTO response = medicamentoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar medicamento", description = "Atualiza os dados de um medicamento existente")
    public ResponseEntity<MedicamentoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MedicamentoRequestDTO request) {
        MedicamentoResponseDTO response = medicamentoService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar medicamentos", description = "Lista todos os medicamentos cadastrados")
    public ResponseEntity<List<MedicamentoResponseDTO>> listar() {
        List<MedicamentoResponseDTO> medicamentos = medicamentoService.listarTodos();
        return ResponseEntity.ok(medicamentos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar medicamento", description = "Busca um medicamento por ID")
    public ResponseEntity<MedicamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        MedicamentoResponseDTO medicamento = medicamentoService.buscarPorId(id);
        return ResponseEntity.ok(medicamento);
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Listar medicamentos por categoria", description = "Lista todos os medicamentos de uma categoria específica")
    public ResponseEntity<List<MedicamentoResponseDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        List<MedicamentoResponseDTO> medicamentos = medicamentoService.listarPorCategoria(categoriaId);
        return ResponseEntity.ok(medicamentos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar medicamento",
               description = "Deleta um medicamento. Se já foi vendido ou tem movimentações de estoque, faz soft delete permanente (não pode ser reativado). Caso contrário, remove fisicamente do banco.")
    public ResponseEntity<MessageResponseDTO> deletar(@PathVariable Long id) {
        String resultado = medicamentoService.deletar(id);

        String mensagem;
        switch (resultado) {
            case "soft_delete_vendido_movimentado":
                mensagem = "Medicamento inativado permanentemente (já foi vendido e possui movimentações de estoque). Não pode ser reativado ou deletado.";
                break;
            case "soft_delete_vendido":
                mensagem = "Medicamento inativado permanentemente (já foi vendido). Não pode ser reativado ou deletado.";
                break;
            case "soft_delete_movimentado":
                mensagem = "Medicamento inativado permanentemente (possui movimentações de estoque). Não pode ser reativado ou deletado.";
                break;
            case "deleted":
                mensagem = "Medicamento deletado com sucesso (removido permanentemente do banco de dados).";
                break;
            default:
                mensagem = "Operação realizada com sucesso.";
        }

        return ResponseEntity.ok(new MessageResponseDTO(mensagem));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status",
               description = "Ativa ou inativa um medicamento temporariamente. Não pode reativar medicamentos que foram inativados permanentemente (soft delete).")
    public ResponseEntity<MessageResponseDTO> alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AlterarStatusDTO request) {
        medicamentoService.alterarStatus(id, request.getAtivo());
        String status = request.getAtivo() ? "ativado" : "inativado";
        return ResponseEntity.ok(new MessageResponseDTO("Medicamento " + status + " com sucesso"));
    }
}
