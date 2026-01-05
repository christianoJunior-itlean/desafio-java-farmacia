package com.farmacia.desafiosjava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCreateResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String cpf;
    private String email;
    private LocalDate dataNascimento;
    private Integer idade;
    private String mensagem;
    private Boolean podeComprar;

    public ClienteCreateResponseDTO(ClienteResponseDTO cliente, Integer idade, String mensagem, Boolean podeComprar) {
        this.id = cliente.getId();
        this.nomeCompleto = cliente.getNomeCompleto();
        this.cpf = cliente.getCpf();
        this.email = cliente.getEmail();
        this.dataNascimento = cliente.getDataNascimento();
        this.idade = idade;
        this.mensagem = mensagem;
        this.podeComprar = podeComprar;
    }
}

