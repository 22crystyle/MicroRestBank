package com.example.bankcards.dto.pagination;

import com.example.bankcards.dto.response.AccountResponse;

import java.util.List;

public record PageAccountResponse(
        List<AccountResponse> content,
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
