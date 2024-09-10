package com.example.chatserveruser.global.constant;

public final class Constants {
    private Constants() {
    }

    public static final String CHAT_DESTINATION = "/sub/chat/";
    public static final String REDIS_CHAT_PREFIX = "chat_";
    public static final String COOKIE_AUTH_HEADER = "Authorization";
    public static final String REDIS_REFRESH_KEY = "REFRESH_TOKEN:";
    public static final String REDIS_SUBSCRIBE_KEY = "SUBSCRIBE:";

    // kafka 상수
    public static final String KAFKA_USER_TO_CHAT_TOPIC = "email";  // chat 인스턴스에 전파하기 위한 토픽
}
