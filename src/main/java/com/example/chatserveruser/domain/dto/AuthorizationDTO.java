package com.example.chatserveruser.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthorizationDTO {
    private String email;
    private String accessToken;
}
