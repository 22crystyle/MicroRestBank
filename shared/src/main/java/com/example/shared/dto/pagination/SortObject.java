package com.example.shared.dto.pagination;

public record SortObject(
        boolean sorted,
        boolean empty,
        boolean unsorted
) {
}
