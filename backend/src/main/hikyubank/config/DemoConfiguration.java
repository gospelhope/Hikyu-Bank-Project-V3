package hikyubank.config;

import hikyubank.application.model.DemoUser;
import hikyubank.dataaccess.repository.DemoUserRepository;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoConfiguration {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ApplicationRunner seedUser(
        DemoUserRepository users,
        PasswordEncoder encoder,
        @Value("${hikyu.demo.password}") String password,
        @Value("${hikyu.demo.pin}") String pin
    ) {
        return arguments -> users.save(new DemoUser(
            "chengyang.lee", "Chengyang Lee", "Chengyang", "Lee",
            encoder.encode(password), encoder.encode(pin)
        ));
    }
}
