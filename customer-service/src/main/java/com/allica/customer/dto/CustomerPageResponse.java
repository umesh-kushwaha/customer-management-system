package com.allica.customer.dto;

import java.util.List;

public record CustomerPageResponse(
        List<CustomerResponseDTO> items,
        PageInfo pageInfo
) {}
