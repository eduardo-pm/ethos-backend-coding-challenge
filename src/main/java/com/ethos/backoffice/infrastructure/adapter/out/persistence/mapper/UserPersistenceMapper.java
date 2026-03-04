package com.ethos.backoffice.infrastructure.adapter.out.persistence.mapper;

import com.ethos.backoffice.domain.model.Role;
import com.ethos.backoffice.domain.model.User;
import com.ethos.backoffice.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserJpaEntity toEntity(User user) {
        return UserJpaEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    public User toDomain(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .name(entity.getName())
                .role(Role.valueOf(entity.getRole()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
