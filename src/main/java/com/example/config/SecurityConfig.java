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

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonLoginFilter() throws Exception {
        var filter = new JsonUsernamePasswordAuthenticationFilter(
                "/auth/login", authenticationManager()
        );

        filter.setAuthenticationSuccessHandler((req, res, auth) ->
                res.setStatus(HttpStatus.OK.value())
        );

        filter.setAuthenticationFailureHandler((req, res, ex) ->
                res.sendError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage())
        );
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/**", "/api/members").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterAt(jsonLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
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
