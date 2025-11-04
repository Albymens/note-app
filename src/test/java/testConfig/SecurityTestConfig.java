package testConfig;

import com.albymens.note_app.config.JwtAuthenticationFilter;
import com.albymens.note_app.service.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;

@TestConfiguration
@ActiveProfiles("test")
public class SecurityTestConfig {
    @Bean
    public JwtService jwtService() {
        return mock(JwtService.class);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
