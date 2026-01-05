package com.farmacia.desafiosjava.service;

import com.farmacia.desafiosjava.domain.Categoria;
import com.farmacia.desafiosjava.domain.Medicamento;
import com.farmacia.desafiosjava.dto.CategoriaResponseDTO;
import com.farmacia.desafiosjava.dto.MedicamentoRequestDTO;
import com.farmacia.desafiosjava.dto.MedicamentoResponseDTO;
import com.farmacia.desafiosjava.exception.BusinessException;
import com.farmacia.desafiosjava.exception.ResourceNotFoundException;
import com.farmacia.desafiosjava.repository.CategoriaRepository;
import com.farmacia.desafiosjava.repository.EstoqueRepository;
import com.farmacia.desafiosjava.repository.MedicamentoRepository;
import com.farmacia.desafiosjava.repository.MovimentacaoEstoqueRepository;
import com.farmacia.desafiosjava.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final EstoqueRepository estoqueRepository;

    @Transactional
    public MedicamentoResponseDTO criar(MedicamentoRequestDTO request) {
        String nomeNormalizado = StringUtils.normalizeString(request.getNome());

        // Validar se já existe medicamento com mesmo nome E dosagem
        if (medicamentoRepository.existsByNomeNormalizadoAndDosagem(nomeNormalizado, request.getDosagem())) {
            throw new BusinessException("Já existe um medicamento com este nome e dosagem");
        }

        Medicamento medicamento = new Medicamento();
        medicamento.setNome(request.getNome());
        medicamento.setDescricao(request.getDescricao());
        medicamento.setDosagem(request.getDosagem());
        medicamento.setPreco(request.getPreco());
        medicamento.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            medicamento.setCategoria(categoria);
        }

        medicamento = medicamentoRepository.save(medicamento);

        // Estoque será criado automaticamente na primeira entrada via POST /estoque/entrada
        // Isso garante que o lote seja criado junto e o sistema FIFO funcione corretamente

        return toResponseDTO(medicamento);
    }

    @Transactional
    public MedicamentoResponseDTO atualizar(Long id, MedicamentoRequestDTO request) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));

        String nomeNormalizado = StringUtils.normalizeString(request.getNome());

        // Verifica se já existe outro medicamento com mesmo nome E dosagem
        if (medicamentoRepository.existsByNomeNormalizadoAndDosagemAndIdNot(nomeNormalizado, request.getDosagem(), id)) {
            throw new BusinessException("Já existe um medicamento com este nome e dosagem");
        }

        medicamento.setNome(request.getNome());
        medicamento.setDescricao(request.getDescricao());
        medicamento.setDosagem(request.getDosagem());
        medicamento.setPreco(request.getPreco());
        medicamento.setAtivo(request.getAtivo());

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            medicamento.setCategoria(categoria);
        } else {
            medicamento.setCategoria(null);
        }

        medicamento = medicamentoRepository.save(medicamento);
        return toResponseDTO(medicamento);
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarTodos() {
        return medicamentoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedicamentoResponseDTO buscarPorId(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));
        return toResponseDTO(medicamento);
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarPorCategoria(Long categoriaId) {
        // Verifica se a categoria existe
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoria não encontrada");
        }

        return medicamentoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public String deletar(Long id) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));

        // Se já foi deletado permanentemente (soft delete), não pode fazer nada
        if (medicamento.getDeletado()) {
            throw new BusinessException("Este medicamento já foi inativado permanentemente e não pode ser alterado");
        }

        // Verificar APENAS se medicamento foi vendido (requisito da história do usuário)
        boolean foiVendido = medicamentoRepository.foiVendido(id);

        // Se foi vendido, fazer soft delete permanente
        if (foiVendido) {
            medicamento.setAtivo(false);
            medicamento.setDeletado(true);
            medicamentoRepository.save(medicamento);
            return "soft_delete_vendido"; // Soft delete: foi vendido
        } else {
            // Caso contrário, deletar fisicamente do banco
            // Movimentações de estoque não impedem delete físico

            // 1. Deletar movimentações de estoque
            movimentacaoEstoqueRepository.deleteByMedicamentoId(id);

            // 2. Deletar estoques (lotes)
            estoqueRepository.deleteByMedicamentoId(id);

            // 3. Deletar o medicamento
            medicamentoRepository.deleteById(id);

            return "deleted"; // Deletado fisicamente
        }
    }

    @Transactional
    public void alterarStatus(Long id, Boolean ativo) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));

        // Não permite reativar medicamentos que foram soft deleted (vendidos)
        if (medicamento.getDeletado()) {
            throw new BusinessException("Este medicamento foi inativado permanentemente e não pode ser reativado");
        }

        medicamento.setAtivo(ativo);
        medicamentoRepository.save(medicamento);
    }

    private MedicamentoResponseDTO toResponseDTO(Medicamento medicamento) {
        MedicamentoResponseDTO dto = new MedicamentoResponseDTO();
        dto.setId(medicamento.getId());
        dto.setNome(medicamento.getNome());
        dto.setDescricao(medicamento.getDescricao());
        dto.setDosagem(medicamento.getDosagem());
        dto.setPreco(medicamento.getPreco());
        dto.setAtivo(medicamento.getAtivo());
        dto.setCriadoEm(medicamento.getCriadoEm());

        if (medicamento.getCategoria() != null) {
            CategoriaResponseDTO categoriaDTO = new CategoriaResponseDTO(
                    medicamento.getCategoria().getId(),
                    medicamento.getCategoria().getNome()
            );
            dto.setCategoria(categoriaDTO);
        }

        return dto;
    }
}
