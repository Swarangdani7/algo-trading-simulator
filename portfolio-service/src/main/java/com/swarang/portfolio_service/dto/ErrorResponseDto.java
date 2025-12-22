package com.swarang.portfolio_service.dto;

import lombok.Builder;

@Builder
public record ErrorResponseDto(
        String message,
        String error,
        int status,
        String timestamp
) { }
