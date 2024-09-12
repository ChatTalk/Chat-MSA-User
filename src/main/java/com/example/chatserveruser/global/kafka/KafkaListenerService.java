package com.example.chatserveruser.global.kafka;

import com.example.chatserveruser.domain.dto.AuthorizationDTO;
import com.example.chatserveruser.domain.dto.UserDTO;
import com.example.chatserveruser.global.security.service.JwtTokenService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.example.chatserveruser.global.constant.Constants.KAFKA_OTHER_TO_USER_TOPIC;
import static com.example.chatserveruser.global.constant.Constants.KAFKA_USER_TO_CHAT_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final JwtTokenService jwtTokenService;
    private final KafkaTemplate<String, AuthorizationDTO> kafkaTemplate;

    // 필요한 다른 곳에서 동일한 어노테이션을 할당하고 메세지를 받으면 됨
    @KafkaListener(topics = KAFKA_OTHER_TO_USER_TOPIC, groupId = "chat")
    public void listen(String beforeToken) {
        log.info("수신한 날 것의 엑세스 토큰: {} ", beforeToken);

        // 레디스에 beforeToken - email 로 저장된 캐싱 조회해서 없으면 슉슉

        if (beforeToken == null) throw new JwtException("엑세스 토큰이 존재하지 않습니다.");

        String tokenValue = jwtTokenService.extractValue(beforeToken);
        jwtTokenService.validAccessToken(tokenValue);

        log.info("정상 확인 후, 추출된 토큰: {}", tokenValue);

        UserDTO userDTO = jwtTokenService.getUserFromToken(tokenValue);
        String email = userDTO.getEmail();

        AuthorizationDTO dto = new AuthorizationDTO(email, beforeToken);
        kafkaTemplate.send(KAFKA_USER_TO_CHAT_TOPIC, dto);

        log.info("카프카 송신, 이메일: {} // 토큰: {}", email, beforeToken);
    }
}
