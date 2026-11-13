package br.ufscar.multiloja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Console do H2 (somente desenvolvimento): liberado, sem CSRF e com frames do mesmo site. */
    @Bean
    @Order(1)
    SecurityFilterChain filtroConsoleH2(HttpSecurity http) throws Exception {
        http
            .securityMatcher(AntPathRequestMatcher.antMatcher("/h2-console/**"))
            .authorizeHttpRequests(a -> a.anyRequest().permitAll())
            .csrf(c -> c.disable())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()));
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(a -> a
                .requestMatchers("/", "/produtos/**", "/login", "/cadastro/**", "/css/**", "/error").permitAll()
                .requestMatchers("/loja/**").hasRole("LOJA")
                .anyRequest().hasRole("CLIENTE"))
            .formLogin(f -> f
                .loginPage("/login")
                .successHandler(redirecionarPorPerfil())
                .permitAll())
            .logout(l -> l.logoutSuccessUrl("/"));
        return http.build();
    }

    private AuthenticationSuccessHandler redirecionarPorPerfil() {
        return (request, response, auth) -> {
            boolean loja = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_LOJA"));
            response.sendRedirect(request.getContextPath() + (loja ? "/loja" : "/"));
        };
    }
}