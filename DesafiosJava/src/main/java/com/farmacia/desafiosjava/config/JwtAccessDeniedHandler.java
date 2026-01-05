package com.farmacia.desafiosjava.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmacia.desafiosjava.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        String message = "Acesso negado: Você não tem permissão para acessar este recurso";
        String detalhes = "Seu token de autenticação é válido, mas você não possui as permissões necessárias "
                + "para realizar esta operação. Entre em contato com o administrador do sistema caso acredite "
                + "que deveria ter acesso a este recurso.";

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                message,
                request.getRequestURI(),
                java.util.List.of(detalhes)
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}

