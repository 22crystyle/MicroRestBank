package com.example.customer.dto;

import com.example.customer.dto.response.CustomerResponse;
import com.example.customer.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);
}
