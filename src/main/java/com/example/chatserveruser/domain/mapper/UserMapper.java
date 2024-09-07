package com.example.chatserveruser.domain.mapper;

import com.example.chatserveruser.domain.dto.UserDTO;
import com.example.chatserveruser.domain.entity.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(
                user.getEmail(),
                user.getPassword(),
                user.getPhone(),
                user.getRole()
        );
    }

    public static User toEntity(UserDTO dto, String password) {
        if (dto == null) {
            return null;
        }

        return new User(dto, password);
    }
}
