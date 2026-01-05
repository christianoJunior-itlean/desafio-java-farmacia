package com.farmacia.desafiosjava.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para alteração de status do medicamento")
public class AlterarStatusDTO {

    @NotNull(message = "Status ativo é obrigatório")
    @Schema(description = "Define se o medicamento está ativo ou inativo", example = "true")
    private Boolean ativo;
}

