package com.example.bankcards.dto.pagination;

public record PageableObject(
        int pageNumber,
        int pageSize,
        SortObject sort,
        int offset,
        boolean paged,
        boolean unpaged
) {
}
