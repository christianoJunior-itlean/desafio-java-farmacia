package com.farmacia.desafiosjava.config;

import com.farmacia.desafiosjava.domain.Usuario;
import com.farmacia.desafiosjava.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Verifica se já existe um usuário admin
            if (usuarioRepository.findByUsername("admin@farmacia.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin@farmacia.com");
                admin.setSenha(passwordEncoder.encode("admin123"));
                usuarioRepository.save(admin);
                
                log.info("========================================");
                log.info("Usuário administrador criado com sucesso!");
                log.info("========================================");
                log.info("Email: admin@farmacia.com");
                log.info("Senha: admin123");
                log.info("========================================");
                log.info("IMPORTANTE: Altere esta senha em produção!");
                log.info("========================================");
            } else {
                log.info("Usuário administrador já existe no banco de dados");
            }
        };
    }
}
