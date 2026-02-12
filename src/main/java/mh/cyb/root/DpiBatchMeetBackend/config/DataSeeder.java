package mh.cyb.root.DpiBatchMeetBackend.config;

import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@example.com")) {
                User admin = new User();
                admin.setEmail("admin@example.com");
                admin.setFullName("System Administrator");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(Set.of(Role.ADMINISTRATOR));
                admin.setEnabled(true);
                admin.setAccountNonLocked(true);

                try {
                    userRepository.save(admin);
                    System.out.println("Default admin user created: admin@example.com / admin123");
                } catch (Exception e) {
                    System.out.println("Default admin user creation skipped (already exists or conflict).");
                }
            }
        };
    }
}
