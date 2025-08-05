package com.example.shared.dto.pagination;

import com.example.shared.dto.pagination.SortObject;

public record PageableObject(
        int pageNumber,
        int pageSize,
        SortObject sort,
        int offset,
        boolean paged,
        boolean unpaged
) {
}
