package com.example.chatserveruser.global.security.handler;

import com.example.chatserveruser.domain.dto.UserInfoDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import static com.example.chatserveruser.global.constant.Constants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final RedisTemplate<String, String> authTemplate;
    private final RedisTemplate<String, UserInfoDTO> cacheTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("로그아웃 핸들러 작동");

        String username = request.getHeader("email");
        log.info("헤더 확인(username): {}", username);
        log.info("헤더 확인(role): {}", request.getHeader("role"));

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_AUTH_HEADER)) {
                try {
                    log.info("정상 토큰에서의 로그아웃 처리");
                    authTemplate.delete(REDIS_REFRESH_KEY + username);
                    cacheTemplate.delete(REDIS_ACCESS_KEY + cookie.getValue());
                } catch (Exception e) {
                    log.error("리프레시 토큰 삭제 및 캐시 삭제 중 오류 발생", e);
                    throw e;
                }
                break;
            }
        }
    }
}
