package mh.cyb.root.DpiBatchMeetBackend.config;

import mh.cyb.root.DpiBatchMeetBackend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // API Security — stateless JWT (unchanged behavior)
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Web UI Security — form login with sessions
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/web/auth/**", "/css/**", "/js/**", "/webjars/**", "/images/**", "/error")
                        .permitAll()
                        .requestMatchers("/web/admin/**").hasRole("ADMINISTRATOR")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/web/auth/login")
                        .loginProcessingUrl("/web/auth/do-login")
                        .defaultSuccessUrl("/web/dashboard", true)
                        .failureUrl("/web/auth/login?error=true")
                        .usernameParameter("email")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/web/auth/logout")
                        .logoutSuccessUrl("/web/auth/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
