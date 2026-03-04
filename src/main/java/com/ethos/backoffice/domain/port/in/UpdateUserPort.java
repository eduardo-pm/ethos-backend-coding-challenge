package com.ethos.backoffice.domain.port.in;

import com.ethos.backoffice.domain.model.User;

import java.util.UUID;

public interface UpdateUserPort {
    User updateUser(UUID id, User user);
}
