package com.ethos.backoffice.infrastructure.config;

import com.ethos.backoffice.application.usecase.UserApplicationService;
import com.ethos.backoffice.domain.port.out.UserRepositoryPort;
import com.ethos.backoffice.domain.service.UserService;
import com.ethos.backoffice.infrastructure.security.JwtAuthenticationFilter;
import com.ethos.backoffice.infrastructure.security.JwtService;
import com.ethos.backoffice.infrastructure.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserBeanConfig {

    @Bean
    public UserService userService(UserRepositoryPort userRepositoryPort) {
        return new UserService(userRepositoryPort);
    }

    @Bean
    public UserApplicationService userApplicationService(UserService userService, PasswordEncoder passwordEncoder) {
        return new UserApplicationService(userService, userService, userService, userService, passwordEncoder);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepositoryPort userRepositoryPort) {
        return new UserDetailsServiceImpl(userRepositoryPort);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
                                                            UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
}
