package com.example.chatserveruser.global.config;

import com.example.chatserveruser.domain.dto.AuthorizationDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

// 이메일과 파싱된 엑세스 토큰을 dto 삼아 보내주기 위한 프로듀서
@Configuration
public class KafkaProducerConfig {

    @Value("${kafka.uri}")
    private String uri;

    @Bean
    public ProducerFactory<String, AuthorizationDTO> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, uri);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, AuthorizationDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
