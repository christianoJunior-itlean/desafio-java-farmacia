package com.farmacia.desafiosjava.service;

import com.farmacia.desafiosjava.domain.Estoque;
import com.farmacia.desafiosjava.domain.Medicamento;
import com.farmacia.desafiosjava.domain.MovimentacaoEstoque;
import com.farmacia.desafiosjava.dto.EstoqueResponseDTO;
import com.farmacia.desafiosjava.dto.MovimentacaoEstoqueRequestDTO;
import com.farmacia.desafiosjava.exception.BusinessException;
import com.farmacia.desafiosjava.exception.ResourceNotFoundException;
import com.farmacia.desafiosjava.repository.EstoqueRepository;
import com.farmacia.desafiosjava.repository.MedicamentoRepository;
import com.farmacia.desafiosjava.repository.MovimentacaoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    @Transactional
    public EstoqueResponseDTO registrarEntrada(MovimentacaoEstoqueRequestDTO request) {
        Medicamento medicamento = medicamentoRepository.findById(request.getMedicamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));

        // Validar data de vencimento futura
        if (request.getDataVencimento().isBefore(LocalDate.now())) {
            throw new BusinessException("Data de vencimento deve ser futura");
        }

        // Gerar número de lote se não fornecido
        String numeroLote = request.getObservacao() != null && !request.getObservacao().isEmpty()
            ? request.getObservacao()
            : "LOTE-" + System.currentTimeMillis();

        // Criar nova entrada de estoque (funciona como lote)
        Estoque estoque = new Estoque();
        estoque.setMedicamento(medicamento);
        estoque.setNumeroLote(numeroLote);
        estoque.setQuantidadeDisponivel(request.getQuantidade());
        estoque.setDataVencimento(request.getDataVencimento());
        estoque.setAtivo(true);

        estoque = estoqueRepository.save(estoque);

        // Registrar movimentação
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setMedicamento(medicamento);
        movimentacao.setTipo(MovimentacaoEstoque.TipoMovimentacao.ENTRADA);
        movimentacao.setQuantidade(request.getQuantidade());
        movimentacao.setObservacao("Lote: " + numeroLote + " - Vencimento: " + request.getDataVencimento());
        movimentacaoRepository.save(movimentacao);

        return toResponseDTO(estoque);
    }

    @Transactional
    public EstoqueResponseDTO registrarSaida(MovimentacaoEstoqueRequestDTO request) {
        Medicamento medicamento = medicamentoRepository.findById(request.getMedicamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));

        // Verificar estoque total disponível
        Integer quantidadeTotal = estoqueRepository.calcularQuantidadeTotal(request.getMedicamentoId());
        if (quantidadeTotal < request.getQuantidade()) {
            throw new BusinessException("Estoque insuficiente. Disponível: " + quantidadeTotal);
        }

        // Baixar estoque usando FIFO
        baixarEstoqueFIFO(request.getMedicamentoId(), request.getQuantidade());

        // Registrar movimentação
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setMedicamento(medicamento);
        movimentacao.setTipo(MovimentacaoEstoque.TipoMovimentacao.SAIDA);
        movimentacao.setQuantidade(request.getQuantidade());
        movimentacao.setObservacao(request.getObservacao());
        movimentacaoRepository.save(movimentacao);

        // Retornar primeiro estoque disponível como resposta
        List<Estoque> estoquesDisponiveis = estoqueRepository.findEstoquesDisponiveis(request.getMedicamentoId());
        if (!estoquesDisponiveis.isEmpty()) {
            return toResponseDTO(estoquesDisponiveis.getFirst());
        }

        // Se não houver mais estoque, criar um DTO vazio
        return new EstoqueResponseDTO(null, medicamento.getId(), medicamento.getNome(), 0, null);
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> buscarPorMedicamento(Long medicamentoId) {
        if (!medicamentoRepository.existsById(medicamentoId)) {
            throw new ResourceNotFoundException("Medicamento não encontrado");
        }

        List<Estoque> estoques = estoqueRepository.findAllByMedicamentoId(medicamentoId);
        return estoques.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstoqueResponseDTO buscarEstoqueConsolidado(Long medicamentoId) {
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));

        Integer quantidadeTotal = estoqueRepository.calcularQuantidadeTotal(medicamentoId);

        // Buscar data de vencimento mais próxima
        List<Estoque> estoquesDisponiveis = estoqueRepository.findEstoquesDisponiveis(medicamentoId);
        LocalDate dataVencimento = estoquesDisponiveis.isEmpty() ? null : estoquesDisponiveis.getFirst().getDataVencimento();

        return new EstoqueResponseDTO(null, medicamento.getId(), medicamento.getNome(), quantidadeTotal, dataVencimento);
    }

    @Transactional
    public void baixarEstoque(Long medicamentoId, Integer quantidade) {
        baixarEstoqueFIFO(medicamentoId, quantidade);

        // Registrar movimentação de venda
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado"));

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setMedicamento(medicamento);
        movimentacao.setTipo(MovimentacaoEstoque.TipoMovimentacao.VENDA);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setObservacao("Baixa automática por venda (FIFO)");
        movimentacaoRepository.save(movimentacao);
    }

    private void baixarEstoqueFIFO(Long medicamentoId, Integer quantidade) {
        // Buscar estoques disponíveis ordenados por FIFO
        List<Estoque> estoquesDisponiveis = estoqueRepository.findEstoquesDisponiveis(medicamentoId);

        Integer quantidadeRestante = quantidade;

        for (Estoque estoque : estoquesDisponiveis) {
            if (quantidadeRestante <= 0) break;

            if (estoque.getQuantidadeDisponivel() >= quantidadeRestante) {
                // Este estoque tem quantidade suficiente
                estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() - quantidadeRestante);
                quantidadeRestante = 0;
            } else {
                // Usar todo este estoque e continuar
                quantidadeRestante -= estoque.getQuantidadeDisponivel();
                estoque.setQuantidadeDisponivel(0);
            }

            estoqueRepository.save(estoque);
        }

        if (quantidadeRestante > 0) {
            throw new BusinessException("Estoque insuficiente. Faltam " + quantidadeRestante + " unidades");
        }
    }

    @Transactional(readOnly = true)
    public boolean verificarEstoqueDisponivel(Long medicamentoId, Integer quantidade) {
        Integer quantidadeTotal = estoqueRepository.calcularQuantidadeTotal(medicamentoId);
        return quantidadeTotal >= quantidade;
    }

    @Transactional(readOnly = true)
    public boolean verificarEstoqueDisponivelNaoVencido(Long medicamentoId, Integer quantidade) {
        Integer quantidadeNaoVencida = estoqueRepository.calcularQuantidadeTotalNaoVencida(medicamentoId, LocalDate.now());
        return quantidadeNaoVencida >= quantidade;
    }

    @Transactional(readOnly = true)
    public boolean medicamentoTemVencidos(Long medicamentoId) {
        List<Estoque> vencidos = estoqueRepository.findEstoquesVencidos(medicamentoId, LocalDate.now());
        return !vencidos.isEmpty();
    }

    private EstoqueResponseDTO toResponseDTO(Estoque estoque) {
        return new EstoqueResponseDTO(
                estoque.getId(),
                estoque.getMedicamento().getId(),
                estoque.getMedicamento().getNome(),
                estoque.getQuantidadeDisponivel(),
                estoque.getDataVencimento()
        );
    }
}

