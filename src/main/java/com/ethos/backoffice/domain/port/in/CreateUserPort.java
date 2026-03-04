package com.ethos.backoffice.domain.port.in;

import com.ethos.backoffice.domain.model.User;

public interface CreateUserPort {
    User createUser(User user);
}
