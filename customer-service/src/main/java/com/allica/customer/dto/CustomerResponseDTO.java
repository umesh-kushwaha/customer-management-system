package com.allica.customer.dto;

import java.time.LocalDate;

public record CustomerResponseDTO(
        Long id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth
) {}
