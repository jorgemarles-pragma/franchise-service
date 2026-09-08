package com.pragma.jamarlesf.api.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record ErrorResponse(
        int status,
        String error,
        String message,
        String timestamp
) {
}
