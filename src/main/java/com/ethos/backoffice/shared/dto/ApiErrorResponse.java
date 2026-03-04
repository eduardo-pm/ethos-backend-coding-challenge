package com.ethos.backoffice.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    @Setter
    private Map<String, String> fieldErrors;

    public static ApiErrorResponse of(HttpStatus status, String message) {
        return new ApiErrorResponse(status.value(), status.getReasonPhrase(), message, LocalDateTime.now(), null);
    }
}
