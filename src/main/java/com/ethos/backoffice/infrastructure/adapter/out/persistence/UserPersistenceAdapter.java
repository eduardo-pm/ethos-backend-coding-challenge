package com.ethos.backoffice.infrastructure.adapter.out.persistence;

import com.ethos.backoffice.domain.model.User;
import com.ethos.backoffice.domain.port.out.UserRepositoryPort;
import com.ethos.backoffice.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.ethos.backoffice.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper mapper;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(userJpaRepository.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll(int page, int size) {
        return userJpaRepository.findAll(PageRequest.of(page, size))
                .map(mapper::toDomain)
                .getContent();
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }

    @Override
    public void deleteById(UUID id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
