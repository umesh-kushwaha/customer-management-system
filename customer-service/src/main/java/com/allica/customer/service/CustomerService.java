package com.allica.customer.service;

import com.allica.customer.dto.CustomerRequestDTO;
import com.allica.customer.dto.CustomerResponseDTO;
import com.allica.customer.dto.CustomerPageResponse;
import com.allica.customer.dto.PageInfo;
import com.allica.customer.entity.Customer;
import com.allica.customer.exception.CustomerNotFoundException;
import com.allica.customer.mapper.CustomerMapper;
import com.allica.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;
    // Convert DTO to Entity and Save
    public CustomerResponseDTO saveCustomer(CustomerRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("CustomerRequestDTO must not be null");
        }
        Customer entity = customerMapper.toEntity(dto);
        Customer savedEntity = customerRepository.save(entity);
        return customerMapper.toDto(savedEntity);
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return customerMapper.toDto(customer);
    }

    // Cursor-based pagination: after "cursor" for next, or before "before" for previous
    public CustomerPageResponse getAllCustomers(Long after, Long before, int pageSize, boolean includeTotal) {
        int safePageSize = Math.max(1, pageSize);
        int fetchSize = safePageSize + 1; // fetch one extra to detect next page
        List<Customer> customers;
        boolean isBackward = before != null;

        if (isBackward) {
            customers = customerRepository.findByIdLessThanOrderByIdDesc(before, PageRequest.of(0, fetchSize));
        } else if (after == null) {
            customers = customerRepository.findAllByOrderByIdAsc(PageRequest.of(0, fetchSize));
        } else {
            customers = customerRepository.findByIdGreaterThanOrderByIdAsc(after, PageRequest.of(0, fetchSize));
        }

        boolean hasExtra = customers.size() > safePageSize;
        List<Customer> pageSlice = hasExtra ? customers.subList(0, safePageSize) : customers;
        if (isBackward) {
            Collections.reverse(pageSlice);
        }

        List<CustomerResponseDTO> items = pageSlice.stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());

        Long nextCursor = pageSlice.isEmpty()
                ? null
                : pageSlice.get(pageSlice.size() - 1).getId();
        Long prevCursor = pageSlice.isEmpty()
                ? null
                : pageSlice.get(0).getId();

        boolean hasNext;
        if (isBackward) {
            hasNext = before != null && customerRepository.existsByIdGreaterThanEqual(before);
        } else {
            hasNext = hasExtra;
        }

        Long totalCount = includeTotal ? customerRepository.count() : null;

        PageInfo pageInfo = new PageInfo(nextCursor, prevCursor, safePageSize, hasNext, totalCount);
        return new CustomerPageResponse(items, pageInfo);
    }


}
