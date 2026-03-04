package com.ethos.backoffice.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "404D635166546A576E5A7234753778214125442A472D4B6150645367566B5970";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    void generateToken_ShouldReturnNonBlankToken() {
        UserDetails userDetails = buildUserDetails("test@test.com");

        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_ShouldReturnCorrectEmail() {
        UserDetails userDetails = buildUserDetails("test@test.com");
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("test@test.com");
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        UserDetails userDetails = buildUserDetails("test@test.com");
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDiffers() {
        UserDetails user1 = buildUserDetails("user1@test.com");
        UserDetails user2 = buildUserDetails("user2@test.com");
        String token = jwtService.generateToken(user1);

        assertThat(jwtService.isTokenValid(token, user2)).isFalse();
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForExpiredToken() {
        JwtService expiredJwtService = new JwtService();
        ReflectionTestUtils.setField(expiredJwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(expiredJwtService, "jwtExpiration", -1000L);

        UserDetails userDetails = buildUserDetails("test@test.com");
        String token = expiredJwtService.generateToken(userDetails);

        assertThatThrownBy(() -> expiredJwtService.isTokenValid(token, userDetails));
    }

    private UserDetails buildUserDetails(String email) {
        return new User(email, "password", List.of());
    }
}
