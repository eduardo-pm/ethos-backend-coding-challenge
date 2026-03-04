package com.ethos.backoffice.domain.port.in;

import com.ethos.backoffice.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface GetUserPort {
    User getUserById(UUID id);
    List<User> getAllUsers(int page, int size);
    long countUsers();
}
