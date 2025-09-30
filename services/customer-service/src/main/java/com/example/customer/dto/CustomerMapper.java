package com.example.customer.dto;

import com.example.customer.dto.request.CustomerRequest;
import com.example.customer.dto.response.CustomerResponse;
import com.example.customer.entity.Customer;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between Customer entities, requests, and responses.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper {
    /**
     * Converts a Customer entity to a CustomerResponse DTO.
     * @param customer The Customer entity to convert.
     * @return The converted CustomerResponse DTO.
     */
    CustomerResponse toResponse(Customer customer);

    /**
     * Converts a CustomerRequest DTO to a Customer entity.
     * @param request The CustomerRequest DTO to convert.
     * @return The converted Customer entity.
     */
    Customer toEntity(CustomerRequest request);
}
