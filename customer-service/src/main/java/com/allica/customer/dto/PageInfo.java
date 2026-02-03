package com.allica.customer.dto;

public record PageInfo(
        Long nextCursor,
        Long prevCursor,
        int pageSize,
        boolean hasNext,
        Long totalCount
) {}
