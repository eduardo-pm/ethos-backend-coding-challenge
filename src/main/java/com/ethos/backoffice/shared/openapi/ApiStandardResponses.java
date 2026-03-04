package com.ethos.backoffice.shared.openapi;

import com.ethos.backoffice.shared.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(
            schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(value = ApiResponseExamples.UNAUTHORIZED)
        )
    ),
    @ApiResponse(
        responseCode = "429",
        description = "Too many requests",
        content = @Content(
            examples = @ExampleObject(value = ApiResponseExamples.TOO_MANY_REQUESTS)
        )
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
            schema = @Schema(implementation = ApiErrorResponse.class),
            examples = @ExampleObject(value = ApiResponseExamples.INTERNAL_SERVER_ERROR)
        )
    )
})
public @interface ApiStandardResponses {
}
