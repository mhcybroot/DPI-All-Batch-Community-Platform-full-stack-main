package mh.cyb.root.DpiBatchMeetBackend.config;

import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DataSeederTest {

    @Test
    public void testInitDatabase_CreatesAdminIfEmpty() throws Exception {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        DataSeeder dataSeeder = new DataSeeder();

        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");

        CommandLineRunner runner = dataSeeder.initDatabase(userRepository, passwordEncoder);
        runner.run();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("admin@example.com", savedUser.getEmail());
        assertTrue(savedUser.getRoles().contains(Role.ADMINISTRATOR));
        assertTrue(savedUser.isEnabled());
    }

    @Test
    public void testInitDatabase_DoesNothingIfNotEmpty() throws Exception {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        DataSeeder dataSeeder = new DataSeeder();

        when(userRepository.existsByEmail("admin@example.com")).thenReturn(true);

        CommandLineRunner runner = dataSeeder.initDatabase(userRepository, passwordEncoder);
        runner.run();

        verify(userRepository, never()).save(any(User.class));
    }
}
