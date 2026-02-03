package com.allica.customer.service;

import com.allica.customer.dto.CustomerRequestDTO;
import com.allica.customer.dto.CustomerResponseDTO;
import com.allica.customer.dto.PageInfo;
import com.allica.customer.dto.CustomerPageResponse;
import com.allica.customer.entity.Customer;
import com.allica.customer.mapper.CustomerMapper;
import com.allica.customer.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("Should successfully save a customer and return DTO")
    void saveCustomer_Success() {
        // 1. Arrange (Given)
        CustomerRequestDTO inputDto = new CustomerRequestDTO("John", "Doe", LocalDate.of(1990, 1, 1));
        Customer entity = new Customer(1L, "John", "Doe", LocalDate.of(1990, 1, 1));
        CustomerResponseDTO responseDto = new CustomerResponseDTO(1L, "John", "Doe", LocalDate.of(1990, 1, 1));

        when(customerMapper.toEntity(any(CustomerRequestDTO.class))).thenReturn(entity);
        when(repository.save(any(Customer.class))).thenReturn(entity);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(responseDto);

        // 2. Act (When)
        CustomerResponseDTO result = customerService.saveCustomer(inputDto);

        // 3. Assert (Then)
        assertNotNull(result);
        assertEquals("John", result.firstName());
        verify(repository, times(1)).save(any(Customer.class));
        verify(customerMapper, times(1)).toEntity(any());
        verify(customerMapper, times(1)).toDto(any());
    }

    @Test
    @DisplayName("Should return a list of customer DTOs")
    void getAllCustomers_Success() {
        // 1. Arrange
        Customer customer1 = new Customer(1L, "John", "Doe", LocalDate.of(1990, 1, 1));
        Customer customer2 = new Customer(2L, "Jane", "Smith", LocalDate.of(1992, 2, 2));
        List<Customer> entities = Arrays.asList(customer1, customer2);

        when(repository.findAllByOrderByIdAsc(PageRequest.of(0, 21))).thenReturn(entities);
        // Mocking the mapper behavior for each stream iteration
        when(customerMapper.toDto(customer1)).thenReturn(new CustomerResponseDTO(1L, "John", "Doe", LocalDate.of(1990, 1, 1)));
        when(customerMapper.toDto(customer2)).thenReturn(new CustomerResponseDTO(2L, "Jane", "Smith", LocalDate.of(1992, 2, 2)));

        // 2. Act
        CustomerPageResponse result = customerService.getAllCustomers(null, null, 20, false);

        // 3. Assert
        assertEquals(2, result.items().size());
        assertEquals("John", result.items().get(0).firstName());
        assertEquals("Jane", result.items().get(1).firstName());
        PageInfo pageInfo = result.pageInfo();
        assertEquals(2L, pageInfo.nextCursor());
        assertEquals(1L, pageInfo.prevCursor());
        assertEquals(20, pageInfo.pageSize());
        assertFalse(pageInfo.hasNext());
        assertNull(pageInfo.totalCount());
        verify(repository, times(1)).findAllByOrderByIdAsc(PageRequest.of(0, 21));
    }

    @Test
    @DisplayName(" Should handle null DTO gracefully")
    void saveCustomer_NullDto() {
        // Act & Assert
        // We expect a clear exception when trying to save a null DTO
        assertThrows(IllegalArgumentException.class, () -> customerService.saveCustomer(null));

        // Verify repository was NEVER touched (Safety check)
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate Persistence Exceptions")
    void saveCustomer_DatabaseDown() {
        // Arrange
        CustomerRequestDTO dto = new CustomerRequestDTO("John", "Doe", LocalDate.of(1990, 1, 1));
        when(customerMapper.toEntity(any())).thenReturn(new Customer());

        // Simulate a DB connection failure or Constraint violation
        when(repository.save(any())).thenThrow(new RuntimeException("Database Connection Timeout"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerService.saveCustomer(dto));

        assertEquals("Database Connection Timeout", exception.getMessage());
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void getAllCustomers_EmptyDatabase() {
        // Arrange: Return empty list from DB
        when(repository.findAllByOrderByIdAsc(PageRequest.of(0, 21))).thenReturn(Collections.emptyList());

        // Act
        CustomerPageResponse result = customerService.getAllCustomers(null, null, 20, false);

        // Assert
        assertNotNull(result, "Service should never return null for lists");
        assertTrue(result.items().isEmpty(), "List should be empty");
        PageInfo pageInfo = result.pageInfo();
        assertNull(pageInfo.nextCursor());
        assertNull(pageInfo.prevCursor());
        assertEquals(20, pageInfo.pageSize());
        assertFalse(pageInfo.hasNext());
        assertNull(pageInfo.totalCount());

        // Verify mapping logic was skipped for optimization
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should handle Data Integrity Violations")
    void saveCustomer_DuplicateEntry() {
        // Arrange
        CustomerRequestDTO dto = new CustomerRequestDTO("John", "Doe", LocalDate.of(1990, 1, 1));
        when(customerMapper.toEntity(any())).thenReturn(new Customer());

        // Simulate a DataIntegrityViolation (e.g., unique constraint on name)
        when(repository.save(any())).thenThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate"));

        // Act & Assert
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
                () -> customerService.saveCustomer(dto));
    }

    @Test
    @DisplayName("Should return customer by id")
    void getCustomerById_Success() {
        Customer entity = new Customer(5L, "Jane", "Doe", LocalDate.of(1995, 5, 5));
        when(repository.findById(5L)).thenReturn(java.util.Optional.of(entity));
        when(customerMapper.toDto(entity)).thenReturn(
                new com.allica.customer.dto.CustomerResponseDTO(5L, "Jane", "Doe", LocalDate.of(1995, 5, 5))
        );

        com.allica.customer.dto.CustomerResponseDTO result = customerService.getCustomerById(5L);

        assertEquals(5L, result.id());
        assertEquals("Jane", result.firstName());
        verify(repository, times(1)).findById(5L);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer not found")
    void getCustomerById_NotFound() {
        when(repository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(com.allica.customer.exception.CustomerNotFoundException.class,
                () -> customerService.getCustomerById(99L));
    }
}
