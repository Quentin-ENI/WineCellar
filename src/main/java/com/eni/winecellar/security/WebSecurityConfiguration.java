package com.eni.winecellar.security;

import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Autowired
    private Filter jwtAuthenticationFilter;
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(request -> {
                request
                    // Swagger UI
                    .requestMatchers("/winecellar/api").permitAll()
                    .requestMatchers("/winecellar/swagger-ui/*").permitAll()
                    // API Docs
                    .requestMatchers("/v3/api-docs").permitAll()
                    .requestMatchers("/v3/api-docs/*").permitAll()
                    // JWT Authentication
                    .requestMatchers("/winecellar/auth/**").permitAll()
                    .requestMatchers("/winecellar").permitAll()
                    // Ressources
                    .requestMatchers(HttpMethod.GET, "/winecellar/bottles").permitAll()
                    .requestMatchers(HttpMethod.POST, "/winecellar/bottles").hasRole("OWNER")
                    .requestMatchers(HttpMethod.PUT, "/winecellar/bottles").hasRole("OWNER")
                    .requestMatchers(HttpMethod.GET, "/winecellar/bottles/region/*").hasAnyRole("CUSTOMER", "OWNER")
                    .requestMatchers(HttpMethod.GET, "/winecellar/bottles/color/*").hasAnyRole("CUSTOMER", "OWNER")
                    .requestMatchers(HttpMethod.GET, "/winecellar/bottles/*").hasAnyRole("CUSTOMER", "OWNER")
                    .requestMatchers(HttpMethod.GET, "/winecellar/region/*").hasRole("OWNER")
                    .requestMatchers(HttpMethod.GET, "/winecellar/color/*").hasRole("OWNER")
                    .requestMatchers(HttpMethod.GET, "/winecellar/shopping_carts/*").hasAnyRole("CUSTOMER", "OWNER")
                    .requestMatchers(HttpMethod.GET, "/winecellar/shopping_carts/customer/active/*").hasAnyRole("CUSTOMER", "OWNER")
                    .requestMatchers(HttpMethod.GET, "/winecellar/shopping_carts/customer/orders/*").hasAnyRole("CUSTOMER", "OWNER")
                    .requestMatchers(HttpMethod.POST, "/winecellar/shopping_carts").hasRole("CUSTOMER")
                    .requestMatchers(HttpMethod.PUT, "/winecellar/shopping_carts").hasRole("CUSTOMER")
                    .anyRequest().denyAll();
            });

        http.csrf(csrf -> {
            csrf.disable();
        });

        http.authenticationProvider(authenticationProvider);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        return http.build();
    }
}