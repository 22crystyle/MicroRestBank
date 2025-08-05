package com.example.shared.dto;

import com.example.shared.dto.request.UserRequest;
import com.example.shared.dto.response.UserResponse;
import com.example.shared.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest dto);

    UserResponse toResponse(User user);
}