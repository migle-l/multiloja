package br.ufscar.multiloja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/** Autenticação com Spring Security: login por formulário, senhas com BCrypt e perfis CLIENTE e LOJA. */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Senhas são gravadas apenas como hash BCrypt (RNF002). */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
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
