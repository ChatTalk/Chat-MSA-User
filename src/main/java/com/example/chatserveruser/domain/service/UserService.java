package com.example.chatserveruser.domain.service;

import com.example.chatserveruser.domain.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDTO createUser(UserDTO userDto);
    UserDTO getUserInfo(String email);
    boolean existUserEmail(String email);
}
