package com.farmacia.desafiosjava.service;

import com.farmacia.desafiosjava.domain.Categoria;
import com.farmacia.desafiosjava.dto.CategoriaRequestDTO;
import com.farmacia.desafiosjava.dto.CategoriaResponseDTO;
import com.farmacia.desafiosjava.exception.BusinessException;
import com.farmacia.desafiosjava.exception.ResourceNotFoundException;
import com.farmacia.desafiosjava.repository.CategoriaRepository;
import com.farmacia.desafiosjava.repository.MedicamentoRepository;
import com.farmacia.desafiosjava.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final MedicamentoRepository medicamentoRepository;

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO request) {
        String nomeNormalizado = StringUtils.normalizeString(request.getNome());

        if (categoriaRepository.existsByNomeNormalizado(nomeNormalizado)) {
            throw new BusinessException("Já existe uma categoria com este nome (independente de acentos ou maiúsculas/minúsculas)");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(request.getNome());

        categoria = categoriaRepository.save(categoria);
        return toResponseDTO(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        return toResponseDTO(categoria);
    }

    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        String nomeNormalizado = StringUtils.normalizeString(request.getNome());

        // Verifica se o nome foi alterado e se já existe outra categoria com esse nome normalizado
        if (categoriaRepository.existsByNomeNormalizadoAndIdNot(nomeNormalizado, id)) {
            throw new BusinessException("Já existe uma categoria com este nome (independente de acentos ou maiúsculas/minúsculas)");
        }

        categoria.setNome(request.getNome());
        categoria = categoriaRepository.save(categoria);
        return toResponseDTO(categoria);
    }

    @Transactional
    public void deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada");
        }

        List<?> medicamentos = medicamentoRepository.findByCategoriaId(id);
        if (!medicamentos.isEmpty()) {
            throw new BusinessException("Não é possível excluir categoria com medicamentos vinculados");
        }

        categoriaRepository.deleteById(id);
    }

    private CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNome());
    }
}
