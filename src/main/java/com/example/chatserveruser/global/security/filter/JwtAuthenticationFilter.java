package com.example.chatserveruser.global.security.filter;

import com.example.chatserveruser.domain.dto.UserDTO;
import com.example.chatserveruser.domain.dto.UserInfoDTO;
import com.example.chatserveruser.domain.entity.UserRoleEnum;
import com.example.chatserveruser.domain.service.UserService;
import com.example.chatserveruser.global.security.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.example.chatserveruser.global.constant.Constants.COOKIE_AUTH_HEADER;


@RequiredArgsConstructor
@Slf4j(topic = "JwtAuthenticationFilter")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, UserInfoDTO> cacheTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 경로를 명시적으로 설정
        if (requestURI.startsWith("/api/users/signup") || requestURI.startsWith("/api/users/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("인증 시도: {}", requestURI);
        String beforeToken = findAccessToken(request.getCookies());
        log.info("초기 토큰값: {}", beforeToken);

        String username = request.getHeader("email");
        log.info("헤더 확인(username): {}", username);
        log.info("헤더 확인(role): {}", request.getHeader("role"));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(createAuthentication(username));
        SecurityContextHolder.setContext(context);
    }

    // Authentication 객체 생성 (UPAT 생성)
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String findAccessToken(Cookie[] cookies){
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_AUTH_HEADER)) return cookie.getValue();
        }
        return null;
    }
}
