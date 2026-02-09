package com.allica.customer.controller;

import com.allica.customer.dto.CustomerRequestDTO;
import com.allica.customer.dto.CustomerResponseDTO;
import com.allica.customer.dto.CustomerPageResponse;
import com.allica.customer.dto.PageInfo;
import com.allica.customer.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // We mock the service so we only test the controller logic
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper; // To convert objects to JSON strings

    @Test
    @DisplayName("Controller: POST should return 201 Created and saved customer")
    void createCustomer_Success() throws Exception {
        CustomerRequestDTO inputDto = new CustomerRequestDTO("Jane", "Doe", LocalDate.of(1995, 5, 5));
        CustomerResponseDTO responseDto = new CustomerResponseDTO(
                1L,
                "Jane",
                "Doe",
                LocalDate.of(1995, 5, 5),
                java.time.LocalDateTime.of(2024, 1, 1, 10, 0),
                java.time.LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        when(customerService.saveCustomer(any(CustomerRequestDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("Controller: GET should return paged customers with nextCursor")
    void getAllCustomers_Success() throws Exception {
        CustomerResponseDTO dto = new CustomerResponseDTO(
                1L,
                "Jane",
                "Doe",
                LocalDate.of(1995, 5, 5),
                java.time.LocalDateTime.of(2024, 1, 1, 10, 0),
                java.time.LocalDateTime.of(2024, 1, 1, 10, 0)
        );
        CustomerPageResponse response = new CustomerPageResponse(
                List.of(dto),
                new PageInfo(10L, 5L, 20, false, null)
        );

        when(customerService.getAllCustomers(null, null, 20, false)).thenReturn(response);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].firstName").value("Jane"))
                .andExpect(jsonPath("$.pageInfo.nextCursor").value(10))
                .andExpect(jsonPath("$.pageInfo.prevCursor").value(5))
                .andExpect(jsonPath("$.pageInfo.pageSize").value(20))
                .andExpect(jsonPath("$.pageInfo.hasNext").value(false));
    }

    @Test
    @DisplayName("Controller: GET by id should return customer")
    void getCustomerById_Success() throws Exception {
        CustomerResponseDTO dto = new CustomerResponseDTO(
                2L,
                "John",
                "Smith",
                LocalDate.of(1990, 1, 1),
                java.time.LocalDateTime.of(2024, 1, 1, 10, 0),
                java.time.LocalDateTime.of(2024, 1, 1, 10, 0)
        );
        when(customerService.getCustomerById(2L)).thenReturn(dto);

        mockMvc.perform(get("/api/customers/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    @DisplayName("Controller: Should return 400 Bad Request on invalid input")
    void createCustomer_ValidationError() throws Exception {
        // Missing lastName (Validation should fail)
        CustomerRequestDTO invalidDto = new CustomerRequestDTO("Jane", "", LocalDate.of(1995, 5, 5));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Negative Test: Should return 400 when firstName is empty")
    void createCustomer_InvalidFirstName_Returns400() throws Exception {
        // Arrange: firstName is empty, which violates @NotBlank
        CustomerRequestDTO invalidDto = new CustomerRequestDTO("", "Doe", LocalDate.of(1990, 1, 1));

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                // Architect move: Check if the error response contains the field name
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.firstName").exists());

        // Verify: The service method should NEVER be called
        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    @DisplayName("Negative Test: Should return 400 when dateOfBirth is in the future")
    void createCustomer_FutureDate_Returns400() throws Exception {
        // Arrange: Date in the future violates @Past
        CustomerRequestDTO invalidDto = new CustomerRequestDTO("John", "Doe", LocalDate.of(2099, 1, 1));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).saveCustomer(any());
    }

    @Test
    @DisplayName("Negative Test: Should return 400 for Malformed JSON")
    void createCustomer_MalformedJson_Returns400() throws Exception {
        // Arrange: Sending a broken JSON string
        String malformedJson = "{ \"firstName\": \"John\", \"lastName\": "; // Missing closing brace/value

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }
}
