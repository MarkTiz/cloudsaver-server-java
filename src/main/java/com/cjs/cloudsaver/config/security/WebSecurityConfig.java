package com.cjs.cloudsaver.config.security;

import com.cjs.cloudsaver.config.jwt.JwtAuthorizationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity()
public class WebSecurityConfig {

    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private final CustomLogoutSuccessHandler logoutSuccessHandler;

    private final JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter;

    private final AppUserDetailService appUserDetailService;

    public WebSecurityConfig(CustomAuthenticationSuccessHandler authenticationSuccessHandler,
                             CustomLogoutSuccessHandler logoutSuccessHandler,
                             JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter,
                             AppUserDetailService appUserDetailService,
                             CustomAuthenticationFailureHandler authenticationFailureHandler,
                             CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.jwtAuthorizationTokenFilter = jwtAuthorizationTokenFilter;
        this.appUserDetailService = appUserDetailService;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    public static final String[] AUTH_WHITE_LIST = {
            "/user/login",
            "/user/register",
            "/tele-images/",
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(configurationSource()))
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(AUTH_WHITE_LIST).permitAll()
                        .anyRequest().authenticated())
                .userDetailsService(appUserDetailService)
                .formLogin(form -> form
                        .loginProcessingUrl("/user/login")
                        .failureHandler(authenticationFailureHandler)
                        .successHandler(authenticationSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessHandler(logoutSuccessHandler))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint));

        http.addFilterBefore(jwtAuthorizationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthorizationTokenFilter, LogoutFilter.class);
        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CorsConfigurationSource configurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setExposedHeaders(List.of("*"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
