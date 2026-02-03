package com.allica.customer.mapper;

import com.allica.customer.dto.CustomerRequestDTO;
import com.allica.customer.dto.CustomerResponseDTO;
import com.allica.customer.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponseDTO toDto(Customer entity) {
        if (entity == null) return null;
        return new CustomerResponseDTO(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDateOfBirth()
        );
    }

    public Customer toEntity(CustomerRequestDTO dto) {
        if (dto == null) return null;
        Customer entity = new Customer();
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setDateOfBirth(dto.dateOfBirth());
        return entity;
    }
}
