package com.farmacia.desafiosjava.service;

import com.farmacia.desafiosjava.domain.Cliente;
import com.farmacia.desafiosjava.dto.ClienteCreateResponseDTO;
import com.farmacia.desafiosjava.dto.ClienteRequestDTO;
import com.farmacia.desafiosjava.dto.ClienteResponseDTO;
import com.farmacia.desafiosjava.exception.BusinessException;
import com.farmacia.desafiosjava.exception.ResourceNotFoundException;
import com.farmacia.desafiosjava.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteCreateResponseDTO criar(ClienteRequestDTO request) {
        if (clienteRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }

        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        // Calcular idade antes de criar o cliente
        int idade = Period.between(request.getDataNascimento(), LocalDate.now()).getYears();

        // Validar responsável para menores de 18 anos
        if (idade < 18) {
            if (request.getNomeResponsavel() == null || request.getNomeResponsavel().trim().isEmpty()) {
                throw new BusinessException("Nome do responsável legal é obrigatório para clientes menores de 18 anos");
            }
        }

        Cliente cliente = new Cliente();
        cliente.setNomeCompleto(request.getNomeCompleto());
        cliente.setCpf(request.getCpf());
        cliente.setEmail(request.getEmail());
        cliente.setDataNascimento(request.getDataNascimento());
        cliente.setNomeResponsavel(request.getNomeResponsavel());

        cliente = clienteRepository.save(cliente);

        // Gerar mensagem apropriada
        String mensagem;
        boolean podeComprar;

        if (idade < 18) {
            mensagem = "Cliente cadastrado com sucesso! ⚠️ ATENÇÃO: Cliente menor de 18 anos (" + idade +
                       " anos). Responsável legal cadastrado: " + cliente.getNomeResponsavel() + ". " +
                       "Este cliente NÃO pode realizar compras diretamente. " +
                       "Compras devem ser realizadas pelo responsável legal.";
            podeComprar = false;
        } else {
            mensagem = "Cliente cadastrado com sucesso! Cliente possui " + idade +
                       " anos e está autorizado a realizar compras.";
            podeComprar = true;
        }

        return new ClienteCreateResponseDTO(
            toResponseDTO(cliente),
            idade,
            mensagem,
            podeComprar
        );
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        // Verifica CPF único
        if (!cliente.getCpf().equals(request.getCpf()) &&
                clienteRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }

        // Verifica Email único
        if (!cliente.getEmail().equals(request.getEmail()) &&
                clienteRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        // Validar responsável para menores de 18 anos
        int idade = Period.between(request.getDataNascimento(), LocalDate.now()).getYears();
        if (idade < 18) {
            if (request.getNomeResponsavel() == null || request.getNomeResponsavel().trim().isEmpty()) {
                throw new BusinessException("Nome do responsável legal é obrigatório para clientes menores de 18 anos");
            }
        }

        cliente.setNomeCompleto(request.getNomeCompleto());
        cliente.setCpf(request.getCpf());
        cliente.setEmail(request.getEmail());
        cliente.setDataNascimento(request.getDataNascimento());
        cliente.setNomeResponsavel(request.getNomeResponsavel());

        cliente = clienteRepository.save(cliente);
        return toResponseDTO(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        return toResponseDTO(cliente);
    }

    public boolean clienteTemIdadeMinima(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        int idade = Period.between(cliente.getDataNascimento(), LocalDate.now()).getYears();
        return idade >= 18;
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNomeCompleto(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getDataNascimento(),
                cliente.getNomeResponsavel()
        );
    }
}
