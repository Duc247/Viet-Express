package vn.DucBackend.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration
 * - Session-based authentication cho Thymeleaf web UI
 * - JWT-based authentication cho REST API
 * - CSRF protection enabled cho web, disabled cho API
 * - Role-based access control
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        @Autowired
        private CustomAuthenticationFailureHandler authenticationFailureHandler;

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        /**
         * CORS Configuration cho API
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*")); // Có thể configure specific origins
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
                configuration.setExposedHeaders(List.of("Authorization"));
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/api/**", configuration);
                return source;
        }

        /**
         * Security Filter Chain cho REST API (JWT-based, Stateless)
         * Ưu tiên cao hơn (Order 1) để xử lý /api/** trước
         */
        @Bean
        @Order(1)
        public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Chỉ áp dụng cho /api/**
                                .securityMatcher("/api/**")

                                // CORS enabled cho API
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // Disable CSRF cho API (dùng JWT thay thế)
                                .csrf(csrf -> csrf.disable())

                                // Stateless session cho API
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Authorization rules cho API
                                .authorizeHttpRequests(auth -> auth
                                                // Public API endpoints
                                                .requestMatchers("/api/auth/login", "/api/auth/register",
                                                                "/api/auth/refresh", "/api/auth/check-email",
                                                                "/api/auth/check-phone")
                                                .permitAll()

                                                // Role-based API access
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/api/manager/**").hasRole("MANAGER")
                                                .requestMatchers("/api/staff/**").hasRole("STAFF")
                                                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                                                .requestMatchers("/api/shipper/**").hasRole("SHIPPER")

                                                // All other API requests require authentication
                                                .anyRequest().authenticated())

                                // Exception handling cho API
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                                // Thêm JWT filter
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /**
         * Security Filter Chain cho Web UI (Session-based, Form Login)
         * Ưu tiên thấp hơn (Order 2) để xử lý các request khác
         */
        @Bean
        @Order(2)
        public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                // CSRF protection - enabled cho forms
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                                // Disable CSRF cho H2 console (dev only)
                                                .ignoringRequestMatchers("/h2-console/**"))

                                // Authorization rules
                                .authorizeHttpRequests(auth -> auth
                                                // Public resources
                                                .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/images/**",
                                                                "/webjars/**")
                                                .permitAll()
                                                .requestMatchers("/error", "/error/**").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll() // Dev only
                                                .requestMatchers("/public/**").permitAll() // Public pages
                                                .requestMatchers("/services/**").permitAll() // Public services
                                                .requestMatchers("/casestudy/**").permitAll() // Public case study
                                                                                              // detail
                                                .requestMatchers("/tracking", "/request").permitAll() // Public tracking
                                                                                                      // & request

                                                // Role-based access
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/manager/**").hasRole("MANAGER")
                                                .requestMatchers("/staff/**").hasRole("STAFF")
                                                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                                                .requestMatchers("/shipper/**").hasRole("SHIPPER")

                                                // All other requests require authentication
                                                .anyRequest().authenticated())

                                // Form login configuration
                                .formLogin(form -> form
                                                .loginPage("/auth/login")
                                                .loginProcessingUrl("/auth/login")
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .defaultSuccessUrl("/auth/login-success", true)
                                                .failureHandler(authenticationFailureHandler)
                                                .permitAll())

                                // Logout configuration
                                .logout(logout -> logout
                                                .logoutUrl("/auth/logout")
                                                .logoutSuccessUrl("/auth/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .clearAuthentication(true)
                                                .permitAll())

                                // Remember me configuration
                                .rememberMe(remember -> remember
                                                .key("uniqueAndSecretKey")
                                                .tokenValiditySeconds(86400) // 1 day
                                                .rememberMeParameter("remember-me"))

                                // Session management
                                .sessionManagement(session -> session
                                                .maximumSessions(1)
                                                .expiredUrl("/auth/login?expired=true"))

                                // Allow H2 console frames (dev only)
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin()))

                                // Exception handling
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/error/403"));

                return http.build();
        }
}
