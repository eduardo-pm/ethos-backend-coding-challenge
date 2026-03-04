package com.ethos.backoffice.infrastructure.adapter.in.web.mapper;

import com.ethos.backoffice.domain.model.User;
import com.ethos.backoffice.shared.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserWebMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
