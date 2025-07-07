package com.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final MemberDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authConfig;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // ➊ JSON 로그인 필터를 빈으로 정의
    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonLoginFilter() throws Exception {
        var filter = new JsonUsernamePasswordAuthenticationFilter(
                "/auth/login", authenticationManager()
        );

        // 로그인 성공 시 200 리턴
        filter.setAuthenticationSuccessHandler((req, res, auth) ->
                res.setStatus(HttpStatus.OK.value())
        );

        // 로그인 실패 시 401 리턴
        filter.setAuthenticationFailureHandler((req, res, ex) ->
                res.sendError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage())
        );

        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ➋ CSRF 완전 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // ➌ 인증 프로바이더 등록
                .authenticationProvider(authenticationProvider())

                // ➍ permitAll
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/members").permitAll()
                        .anyRequest().authenticated()
                )

                // ➎ formLogin 도 꺼 주세요
                .formLogin(AbstractHttpConfigurer::disable)

                // ➏ JSON 필터를 UsernamePasswordAuthenticationFilter 자리로 교체
                .addFilterAt(jsonLoginFilter(), UsernamePasswordAuthenticationFilter.class)

                // ➐ 세션 생성 정책
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // ➑ 로그아웃
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((req, res, auth) ->
                                res.setStatus(HttpStatus.OK.value())
                        )
                )
        ;

        return http.build();
    }
}
