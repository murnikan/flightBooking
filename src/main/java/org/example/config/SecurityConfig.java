package org.example.config;

import Users.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/airlines/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/airlines/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/airlines/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/flights/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/flights/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/flights/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/airports/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/airports/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/airports/**").hasRole("ADMIN")
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers("/api/customers/**")
                        .hasAnyRole("ADMIN", "CUSTOMER")


                        .requestMatchers("/api/managers/**")
                        .hasAnyRole("ADMIN", "MANAGER")


                        .requestMatchers(HttpMethod.GET, "/api/bookings/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/bookings/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasAnyRole("CUSTOMER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/flights/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/airlines/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/airports/**").authenticated()

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManager.class);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
