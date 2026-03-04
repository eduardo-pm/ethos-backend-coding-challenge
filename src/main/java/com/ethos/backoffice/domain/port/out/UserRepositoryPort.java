package com.ethos.backoffice.domain.port.out;

import com.ethos.backoffice.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findAll(int page, int size);
    long count();
    void deleteById(UUID id);
    boolean existsByEmail(String email);
}
