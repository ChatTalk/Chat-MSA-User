package com.example.chatserveruser.global.config;

import com.example.chatserveruser.domain.dto.UserInfoDTO;
import com.example.chatserveruser.domain.service.UserService;
import com.example.chatserveruser.global.security.exception.JwtAccessDenyHandler;
import com.example.chatserveruser.global.security.exception.JwtAuthenticationEntryPoint;
import com.example.chatserveruser.global.security.filter.CustomLoginFilter;
import com.example.chatserveruser.global.security.filter.JwtAuthenticationFilter;
import com.example.chatserveruser.global.security.filter.JwtAuthorizationFilter;
import com.example.chatserveruser.global.security.filter.JwtExceptionFilter;
import com.example.chatserveruser.global.security.handler.CustomLogoutHandler;
import com.example.chatserveruser.global.security.service.JwtTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.chatserveruser.global.constant.Constants.COOKIE_AUTH_HEADER;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomLogoutHandler customLogoutHandler;

    // 인증 매니저 생성
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CustomLoginFilter customLoginFilter() throws Exception {
        CustomLoginFilter filter = new CustomLoginFilter(jwtTokenService);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);  // csrf 토큰 무효화 설정을 해야 인증 예외 허용 가능

        // Security 의 기본 설정인 Session 방식이 아닌 JWT 방식을 사용하기 위한 설정
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // JWT 방식의 REST API 서버이기 때문에 FormLogin 방식, HttpBasic 방식 비활성화
        // 클라이언트가 분리됐으므로 비할성화
        http.formLogin(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/signup").permitAll()
                        .anyRequest().authenticated()
        );

        http.logout(logout -> {
            logout
                    .logoutUrl("/api/users/logout") // 로그아웃 API 엔드포인트
                    .addLogoutHandler(customLogoutHandler)
                    .logoutSuccessHandler((req, res, auth) -> {
                        res.setStatus(HttpServletResponse.SC_OK);  // 로그아웃 성공 시 200 OK 응답
                    })
                    .deleteCookies(COOKIE_AUTH_HEADER);
        });

        // 필터 체인에 필터 추가 및 순서 지정
        http.addFilterBefore(new JwtAuthorizationFilter(), CustomLoginFilter.class);
        http.addFilterBefore(new JwtAuthenticationFilter(userDetailsService), JwtAuthorizationFilter.class);
        http.addFilterBefore(customLoginFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
