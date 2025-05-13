package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.AccountDto;
import com.example.bankcards.entity.Account;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto toDto(Account account);
    Account toEntity(AccountDto accountDto);
}