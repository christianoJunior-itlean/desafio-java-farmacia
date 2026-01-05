package com.farmacia.desafiosjava.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmacia.desafiosjava.exception.ErrorResponse;
import com.farmacia.desafiosjava.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwt = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            handleJwtException(response, request,
                "Token expirado",
                "Seu token de autenticação expirou. Por favor, faça login novamente em /auth/login para obter um novo token.");
        } catch (SignatureException ex) {
            handleJwtException(response, request,
                "Token inválido - assinatura incorreta",
                "O token fornecido possui uma assinatura inválida. Verifique se você está usando o token correto e completo.");
        } catch (MalformedJwtException ex) {
            handleJwtException(response, request,
                "Token malformado",
                "O formato do token está incorreto. Certifique-se de incluir o token completo no formato: Authorization: Bearer {token}");
        } catch (Exception ex) {
            handleJwtException(response, request,
                "Erro na autenticação",
                "Ocorreu um erro ao processar seu token: " + ex.getMessage());
        }
    }

    private void handleJwtException(HttpServletResponse response, HttpServletRequest request,
                                    String message, String detalhes) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                message,
                request.getRequestURI(),
                List.of(detalhes)
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Ignora autenticação para endpoints públicos e de documentação Swagger
        List<String> whitelistPrefixes = List.of(
                "/auth/",
                "/v3/api-docs",
                "/swagger-ui",
                "/swagger-ui.html"
        );

        for (String prefix : whitelistPrefixes) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
