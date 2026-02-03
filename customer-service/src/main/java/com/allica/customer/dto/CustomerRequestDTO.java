package com.allica.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CustomerRequestDTO(
        @NotBlank(message = "First name is mandatory")
        String firstName,

        @NotBlank(message = "Last name is mandatory")
        String lastName,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth
) {}
