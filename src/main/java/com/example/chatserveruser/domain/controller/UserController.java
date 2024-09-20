package com.example.chatserveruser.domain.controller;

import com.example.chatserveruser.domain.dto.UserDTO;
import com.example.chatserveruser.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody UserDTO userDTO) {
        log.info("회원가입 시도 이메일: {}", userDTO.getEmail());
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.OK);
    }

    // 카프카 테스트
    @GetMapping("/test")
    public void test() {
        log.info("카프카 테스트용 송신");
    }
}