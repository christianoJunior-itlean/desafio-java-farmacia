package com.farmacia.desafiosjava.service;

import com.farmacia.desafiosjava.domain.Cliente;
import com.farmacia.desafiosjava.domain.ItemVenda;
import com.farmacia.desafiosjava.domain.Medicamento;
import com.farmacia.desafiosjava.domain.Venda;
import com.farmacia.desafiosjava.dto.*;
import com.farmacia.desafiosjava.exception.BusinessException;
import com.farmacia.desafiosjava.exception.ResourceNotFoundException;
import com.farmacia.desafiosjava.repository.ClienteRepository;
import com.farmacia.desafiosjava.repository.MedicamentoRepository;
import com.farmacia.desafiosjava.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final EstoqueService estoqueService;
    private final ClienteService clienteService;

    @Transactional
    public VendaResponseDTO criar(VendaRequestDTO request) {
        // Validar cliente
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        // Validar idade mínima
        if (!clienteService.clienteTemIdadeMinima(request.getClienteId())) {
            throw new BusinessException("Cliente deve ter 18 anos ou mais para realizar compras");
        }

        // Validar itens
        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new BusinessException("A venda deve ter ao menos um item");
        }

        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setItensVenda(new ArrayList<>());

        BigDecimal valorTotal = BigDecimal.ZERO;

        // Processar cada item
        for (ItemVendaDTO itemDTO : request.getItens()) {
            Medicamento medicamento = medicamentoRepository.findById(itemDTO.getMedicamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicamento não encontrado: " + itemDTO.getMedicamentoId()));

            // Validar medicamento ativo (impede venda de medicamentos inativos ou deletados via soft delete)
            if (!medicamento.getAtivo()) {
                throw new BusinessException("Medicamento inativo não pode ser vendido: " + medicamento.getNome());
            }

            // Validar estoque disponível não vencido
            if (!estoqueService.verificarEstoqueDisponivelNaoVencido(medicamento.getId(), itemDTO.getQuantidade())) {
                // Verifica se tem lotes vencidos para mensagem mais específica
                if (estoqueService.medicamentoTemVencidos(medicamento.getId())) {
                    throw new BusinessException("Não há estoque válido (não vencido) suficiente para o medicamento: " + medicamento.getNome());
                } else {
                    throw new BusinessException("Estoque insuficiente para o medicamento: " + medicamento.getNome());
                }
            }

            // Criar item de venda
            ItemVenda item = new ItemVenda();
            item.setMedicamento(medicamento);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(medicamento.getPreco()); // Preço atual do medicamento
            item.setVenda(venda);

            venda.getItensVenda().add(item);
            valorTotal = valorTotal.add(item.getSubtotal());

            // Baixar estoque
            estoqueService.baixarEstoque(medicamento.getId(), itemDTO.getQuantidade());
        }

        venda.setValorTotal(valorTotal);
        venda = vendaRepository.save(venda);

        return toResponseDTO(venda);
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarTodas() {
        return vendaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO buscarPorId(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada"));
        return toResponseDTO(venda);
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente não encontrado");
        }
        return vendaRepository.findByClienteIdOrderByDataHoraDesc(clienteId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private VendaResponseDTO toResponseDTO(Venda venda) {
        VendaResponseDTO dto = new VendaResponseDTO();
        dto.setId(venda.getId());
        dto.setDataHora(venda.getDataHora());
        dto.setValorTotal(venda.getValorTotal());

        // Cliente
        ClienteResponseDTO clienteDTO = new ClienteResponseDTO(
                venda.getCliente().getId(),
                venda.getCliente().getNomeCompleto(),
                venda.getCliente().getCpf(),
                venda.getCliente().getEmail(),
                venda.getCliente().getDataNascimento(),
                venda.getCliente().getNomeResponsavel()
        );
        dto.setCliente(clienteDTO);

        // Itens
        List<ItemVendaResponseDTO> itensDTO = venda.getItensVenda().stream()
                .map(item -> new ItemVendaResponseDTO(
                        item.getId(),
                        item.getMedicamento().getId(),
                        item.getMedicamento().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());
        dto.setItens(itensDTO);

        return dto;
    }
}
