package hexlet.code.config.security;

import hexlet.code.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Autowired
    final UserDetailsServiceImpl userDetailsServiceImpl;

    private final RequestMatcher publicUrls;

    // Указываем, что для сравнения хешей паролей будет использоваться кодировщик BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Указываем, что будем использовать созданный нами диспетчер аутентификации вместо дефолтного
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Переопределяем схему аутентификации
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // BEGIN
        http
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/**").permitAll()
                        .anyRequest().permitAll()
                );
        return http.build();
        // END
    }
}
