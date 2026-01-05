package com.farmacia.desafiosjava.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @Schema(example = "email@email.com")
    @NotBlank(message = "Username é obrigatório")
    private String username;

    @Schema(example = "senha123")
    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}
