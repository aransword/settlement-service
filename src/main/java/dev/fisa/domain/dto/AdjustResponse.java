package dev.fisa.domain.dto;

public record AdjustResponse(
        String bankCode,
        boolean success,
        String message
) {}