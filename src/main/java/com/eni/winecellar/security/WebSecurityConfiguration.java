package com.eni.winecellar.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select username, password, 1 from wine_cellar_user where username = ?;");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select username, authority from wine_cellar_user where username = ?;");

        return jdbcUserDetailsManager;
     }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//            .csrf().disable()
            .authorizeHttpRequests(request -> {
                request
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

        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }
}