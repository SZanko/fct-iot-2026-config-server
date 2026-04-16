package pt.nova.fct.iot.configserver.service.security

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val paths = PathPatternRequestMatcher.withDefaults()

        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                    .requestMatchers(
                        paths.matcher("/swagger-ui.html"),
                        paths.matcher("/swagger-ui/**"),
                        paths.matcher("/v3/api-docs/**"),
                    ).permitAll()
                    .requestMatchers(paths.matcher("/error")).permitAll()
                    .requestMatchers(paths.matcher(HttpMethod.GET, "/api/iot/**")).permitAll()
                    .anyRequest().denyAll()
            }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }

        return http.build()
    }
}
