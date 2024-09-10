package com.example.chatserveruser.global.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.example.chatserveruser.global.constant.Constants.KAFKA_USER_TO_CHAT_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // 채팅 연결시에 인증 필요를 위한 목적
    // 향후, 기능 확장이 있으면 이걸 인증됐다는 걸 알려주는 수단으로 활용하는 것도 좋을듯
    public void sendMessage(String email) {
        log.info("채팅 인스턴스로 송신하는 인증 이메일 정보: {}", email);
        kafkaTemplate.send(KAFKA_USER_TO_CHAT_TOPIC, email);
    }
}
