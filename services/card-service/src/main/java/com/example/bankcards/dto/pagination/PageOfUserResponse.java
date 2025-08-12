package com.example.bankcards.dto.pagination;


import com.example.bankcards.dto.response.UserResponse;

import java.util.List;

public record PageOfUserResponse(
        List<UserResponse> content,
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
