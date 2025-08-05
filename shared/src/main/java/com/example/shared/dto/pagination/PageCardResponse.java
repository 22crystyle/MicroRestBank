package com.example.shared.dto.pagination;

import com.example.shared.dto.response.CardResponse;

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
