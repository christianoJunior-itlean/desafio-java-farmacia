package com.farmacia.desafiosjava.service;

import com.farmacia.desafiosjava.domain.Estoque;
import com.farmacia.desafiosjava.dto.AlertaEstoqueBaixoDTO;
import com.farmacia.desafiosjava.dto.AlertaValidadeProximaDTO;
import com.farmacia.desafiosjava.repository.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final EstoqueRepository estoqueRepository;

    @Value("${estoque.limite-baixo:10}")
    private Integer limiteBaixo;

    @Value("${validade.dias-alerta:30}")
    private Integer diasAlerta;

    @Transactional(readOnly = true)
    public List<AlertaEstoqueBaixoDTO> buscarEstoqueBaixo() {
        // Buscar todos os estoques ativos e agrupar por medicamento
        List<Estoque> todosEstoques = estoqueRepository.findAll();

        Map<Long, List<Estoque>> estoquesPorMedicamento = todosEstoques.stream()
                .filter(e -> e.getAtivo()
                        && e.getMedicamento().getAtivo()
                        && !e.getMedicamento().getDeletado()) // Não considerar medicamentos deletados
                .collect(Collectors.groupingBy(e -> e.getMedicamento().getId()));

        List<AlertaEstoqueBaixoDTO> alertas = new ArrayList<>();

        for (Map.Entry<Long, List<Estoque>> entry : estoquesPorMedicamento.entrySet()) {
            Integer quantidadeTotal = entry.getValue().stream()
                    .mapToInt(Estoque::getQuantidadeDisponivel)
                    .sum();

            if (quantidadeTotal < limiteBaixo && quantidadeTotal > 0) {
                Estoque primeiroEstoque = entry.getValue().get(0);
                alertas.add(new AlertaEstoqueBaixoDTO(
                        primeiroEstoque.getMedicamento().getId(),
                        primeiroEstoque.getMedicamento().getNome(),
                        quantidadeTotal,
                        limiteBaixo,
                        primeiroEstoque.getMedicamento().getPreco()
                ));
            }
        }

        return alertas;
    }

    @Transactional(readOnly = true)
    public List<AlertaValidadeProximaDTO> buscarValidadeProxima() {
        LocalDate hoje = LocalDate.now();
        LocalDate dataLimite = hoje.plusDays(diasAlerta);

        List<Estoque> estoques = estoqueRepository.findEstoquesComVencimentoProximo(hoje, dataLimite);

        return estoques.stream()
                .map(estoque -> {
                    long dias = ChronoUnit.DAYS.between(hoje, estoque.getDataVencimento());
                    return new AlertaValidadeProximaDTO(
                            estoque.getMedicamento().getId(),
                            estoque.getMedicamento().getNome(),
                            estoque.getQuantidadeDisponivel(),
                            estoque.getDataVencimento(),
                            dias
                    );
                })
                .collect(Collectors.toList());
    }
}
