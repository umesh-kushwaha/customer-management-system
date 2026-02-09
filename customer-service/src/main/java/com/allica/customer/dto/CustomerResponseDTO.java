package com.allica.customer.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerResponseDTO(
        Long id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
