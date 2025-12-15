package org.restbank.service.customer.dto;

import org.mapstruct.Mapper;
import org.restbank.service.customer.dto.request.CustomerRequest;
import org.restbank.service.customer.dto.response.CustomerResponse;
import org.restbank.service.customer.entity.Customer;

/**
 * Mapper interface for converting between Customer entities, requests, and responses.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper {
    /**
     * Converts a Customer entity to a CustomerResponse DTO.
     *
     * @param customer The Customer entity to convert.
     * @return The converted CustomerResponse DTO.
     */
    CustomerResponse toResponse(Customer customer);

    /**
     * Converts a CustomerRequest DTO to a Customer entity.
     *
     * @param request The CustomerRequest DTO to convert.
     * @return The converted Customer entity.
     */
    Customer toEntity(CustomerRequest request);
}
