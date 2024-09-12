package com.example.chatserveruser.global.kafka;

import com.example.chatserveruser.domain.dto.UserDTO;
import com.example.chatserveruser.global.security.service.JwtTokenService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.example.chatserveruser.global.constant.Constants.KAFKA_OTHER_TO_USER_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final JwtTokenService jwtTokenService;
    private final RedisTemplate<String, String> authTemplate;

    // 필요한 다른 곳에서 동일한 어노테이션을 할당하고 메세지를 받으면 됨
    @KafkaListener(topics = KAFKA_OTHER_TO_USER_TOPIC, groupId = "chat")
    public void listen(String beforeToken) {
        log.info("수신한 날 것의 엑세스 토큰: {} ", beforeToken);

        // 레디스에 beforeToken - email 로 저장된 캐싱 조회해서 없으면 슉슉
        if (Boolean.TRUE.equals(authTemplate.hasKey(beforeToken))) {
            log.info("이미 해당 토큰이 캐싱되어 있음: {} ", beforeToken);
            return;
        }

        String tokenValue = jwtTokenService.extractValue(beforeToken);
        jwtTokenService.validAccessToken(tokenValue);

        log.info("정상 확인 후, 추출된 토큰: {}", tokenValue);

        UserDTO userDTO = jwtTokenService.getUserFromToken(tokenValue);
        String email = userDTO.getEmail();

        authTemplate.opsForSet().add(beforeToken, email);
        log.info("레디스 저장, 이메일: {} // 토큰: {}", email, beforeToken);
    }
}
