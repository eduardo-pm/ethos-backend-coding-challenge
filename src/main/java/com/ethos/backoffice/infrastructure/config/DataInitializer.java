package com.ethos.backoffice.infrastructure.config;

import com.ethos.backoffice.domain.model.Role;
import com.ethos.backoffice.domain.model.User;
import com.ethos.backoffice.domain.port.out.UserRepositoryPort;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepositoryPort.existsByEmail("admin@backoffice.com")) {
            User admin = User.builder()
                    .email("admin@backoffice.com")
                    .name("Admin")
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ADMIN)
                    .build();
            userRepositoryPort.save(admin);
        }
    }
}
