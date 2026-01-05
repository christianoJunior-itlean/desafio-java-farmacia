package com.farmacia.desafiosjava.service;

import com.farmacia.desafiosjava.config.JwtUtil;
import com.farmacia.desafiosjava.domain.Usuario;
import com.farmacia.desafiosjava.dto.LoginRequestDTO;
import com.farmacia.desafiosjava.dto.LoginResponseDTO;
import com.farmacia.desafiosjava.exception.BusinessException;
import com.farmacia.desafiosjava.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getSenha())
            );
        } catch (Exception e) {
            throw new BusinessException("Credenciais inválidas");
        }

        String token = jwtUtil.generateToken(request.getUsername());
        return new LoginResponseDTO(token, "Bearer", request.getUsername());
    }

    @Transactional
    public void registrar(LoginRequestDTO request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username já existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole("USER");

        usuarioRepository.save(usuario);
    }
}
