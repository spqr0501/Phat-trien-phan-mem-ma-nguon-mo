package com.mycompany.config;

import com.mycompany.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private UserDetailsServiceImpl userDetailsService;

        // Tắt hoàn toàn mã hoá mật khẩu → dùng pass thường (123, 123456...) được luôn
        @SuppressWarnings("deprecation")
        @Bean
        public PasswordEncoder passwordEncoder() {
                return NoOpPasswordEncoder.getInstance();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .userDetailsService(userDetailsService)
                                // TẮT CSRF hoàn toàn
                                .csrf(csrf -> csrf.disable())
                                // TẮT session fixation protection
                                .sessionManagement(session -> session
                                                .sessionFixation().none())
                                // THÊM: Cache-Control headers để ngăn browser cache HTML
                                .headers(headers -> headers
                                                .cacheControl(cache -> {
                                                }) // Bật cache control mặc định
                                )
                                // Phân quyền truy cập
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/**").permitAll() // REST API cho phép truy cập tự do
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                                                .requestMatchers("/css/**", "/js/**", "/Images/**", "/images/**", "/fonts/**").permitAll()
                                                .anyRequest().permitAll() // Tất cả đều cho phép (dev mode)
                                )
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                                .logoutSuccessUrl("/?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll());

                return http.build();
        }
}