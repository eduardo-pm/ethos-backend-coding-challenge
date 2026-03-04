package com.ethos.backoffice.domain.port.in;

import java.util.UUID;

public interface DeleteUserPort {
    void deleteUser(UUID id);
}
