package com.example.customer.dto.pagination;

import com.example.customer.dto.response.CustomerResponse;
import com.example.shared.dto.pagination.PageableObject;
import com.example.shared.dto.pagination.SortObject;

import java.util.List;

public record PageCustomerResponse(
        List<CustomerResponse> content,
        PageableObject pageable,
        boolean last,
        int totalElements,
        int totalPages,
        boolean first,
        int size,
        int number,
        SortObject sort,
        int numberOfElements,
        boolean empty
) {
}