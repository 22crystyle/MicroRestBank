package com.example.bankcards.dto.pagination;

import com.example.bankcards.dto.response.CardResponse;

import java.util.List;

public record PageCardResponse(
        List<CardResponse> content,
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
