package com.springapi.springapitechnicaltest.configuration;

import com.springapi.springapitechnicaltest.services.UserDetailsServiceApp;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtFilterAuthConfiguration jwtAuthenticationFilter;
    private final UserDetailsServiceApp userDetailsService;
    @Value("${api.request.path}")
    private String apiPath;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(getUserEndpointsWhitelist()).hasRole("USER")
                        .requestMatchers(getAdminEndpointsWhitelist()).hasRole("ADMIN")
                        .anyRequest().permitAll())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","PATCH"));
        configuration.addExposedHeader("Roles");
        configuration.addExposedHeader("Expiration");
        configuration.addExposedHeader("Token");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    private String[] getAdminEndpointsWhitelist(){
        String[] adminEndpointsWhitelist = {
                apiPath + "/admin/enable/user/**",
                apiPath + "/admin/disable/user/**",
                apiPath + "/category/new/**",
                apiPath + "/category/get/**",
                apiPath + "/category/update/**",
                apiPath + "/category/delete/**",
                apiPath + "/category/disable/**",
                apiPath + "/category/enable/**",
                apiPath + "/order/complete/**",
                apiPath + "/order/refund/**",
                apiPath + "/orders/all/get/**",
                apiPath + "/orders/all/get/completed/**",
                apiPath + "/orders/all/get/canceled/**",
                apiPath + "/orders/all/get/refunded/**",
                apiPath + "/product/new/**",
                apiPath + "/product/get/**",
                apiPath + "/product/delete/**",
                apiPath + "/product/update/**",
                apiPath + "/product/disable/**",
                apiPath + "/product/enable/**",
                apiPath + "/category/all/get/{categoryId}"
        };
        return adminEndpointsWhitelist;
    }

    private String[] getUserEndpointsWhitelist(){
        String[] userEndPointsWhitelist = {
                apiPath + "/user/admin/new/**",
                apiPath + "/order/new/**",
                apiPath + "/order/buy/**",
                apiPath + "/order/cancel/**",
                apiPath + "/order/get/**",
                apiPath + "/orders/get/**",
                apiPath + "/orders/get/completed/**",
                apiPath + "/orders/get/canceled/**",
                apiPath + "/orders/get/refunded/**",
                apiPath + "/review/new/**",
                apiPath + "/review/get/**",
                apiPath + "/review/update/**",
                apiPath + "/review/delete/**",
                apiPath + "/cart/get/**",
                apiPath + "/cart/new/**",
                apiPath + "/cart/update/**",
                apiPath + "/cart/delete/**",
                apiPath + "/cart/add/**",
                apiPath + "/product/get/**",
        };
        return userEndPointsWhitelist;
    }

}
