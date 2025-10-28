package id.co.bpddiy.social.config;

import id.co.bpddiy.social.security.CustomUserDetailsService;
import id.co.bpddiy.social.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Configuration
 * Konfigurasi untuk authentication, authorization, dan JWT
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Password encoder menggunakan BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication Provider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication Manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    

    /**
     * Security Filter Chain - Konfigurasi utama security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                    // Public endpoints - tidak perlu authentication
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/public/**").permitAll()

                    // Swagger/OpenAPI documentation - public access
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("/api-docs/**").permitAll()
                        

                    // Health check endpoints
                    .requestMatchers("/actuator/**").permitAll()

                    // Public read operations - tidak perlu login
                    .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/users/*/posts").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()

                    // Admin only endpoints
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAnyRole("ADMIN", "MODERATOR")
                    .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasAnyRole("ADMIN", "MODERATOR")

                    // Authenticated endpoints - perlu login
                    .requestMatchers(HttpMethod.POST, "/api/posts/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/comments/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/comments/**").authenticated()
                    .requestMatchers("/api/users/profile/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll()

                    // Default - semua request lainnya perlu authentication
                    .anyRequest().authenticated()
                );
        // Add JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}