package com.example.bankcards.dto;

import com.example.bankcards.dto.request.AccountRequest;
import com.example.bankcards.dto.response.AccountResponse;
import com.example.bankcards.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toEntity(AccountRequest dto);

    AccountResponse toResponse(Account account);

    AccountResponse toResponse(AccountRequest account);
}