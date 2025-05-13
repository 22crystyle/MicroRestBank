package com.example.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    @NotBlank
    private Long id;
    @NotNull private String username;
    @NotNull private String firstName;
    @NotNull private String lastName;
    @Email private String email;
    private String phone;
    private List<CardDto> bankCards;
}
