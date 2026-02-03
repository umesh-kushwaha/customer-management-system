package com.allica.customer.controller;

import com.allica.customer.dto.CustomerRequestDTO;
import com.allica.customer.dto.CustomerResponseDTO;
import com.allica.customer.dto.CustomerPageResponse;
import com.allica.customer.service.CustomerService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO customer) {
        CustomerResponseDTO savedCustomer = service.saveCustomer(customer);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CustomerPageResponse> getAllCustomers(
            @RequestParam(name = "after", required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(name = "pageSize", defaultValue = "20") @Min(1) @Max(100) int pageSize,
            @RequestParam(defaultValue = "false") boolean includeTotal
    ) {
        if (after != null && before != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Specify only one of 'after' or 'before'."
            );
        }
        return ResponseEntity.ok(service.getAllCustomers(after, before, pageSize, includeTotal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCustomerById(id));
    }
}
