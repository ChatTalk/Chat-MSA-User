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

    public void sendMessage(String email) {
        log.info("채팅 인스턴스로 송신하는 인증 이메일 정보: {}", email);
        kafkaTemplate.send(KAFKA_USER_TO_CHAT_TOPIC, email);
    }
}
