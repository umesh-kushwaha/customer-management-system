package com.allica.customer.mapper;

import com.allica.customer.dto.CustomerResponseDTO;
import com.allica.customer.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    @DisplayName("Mapper: Should map Entity to DTO correctly")
    void toDto_Success() {
        Customer entity = new Customer(
                1L,
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0)
        );

        CustomerResponseDTO dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("John", dto.firstName());
        assertEquals("Doe", dto.lastName());
        assertNotNull(dto.createdAt());
        assertNotNull(dto.updatedAt());
    }

    @Test
    @DisplayName("Mapper: Should return null when entity is null")
    void toDto_NullInput() {
        assertNull(mapper.toDto(null));
    }
}
