package com.farmacia.desafiosjava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {

    private String message;
    private LocalDateTime timestamp;

    public MessageResponseDTO(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}

