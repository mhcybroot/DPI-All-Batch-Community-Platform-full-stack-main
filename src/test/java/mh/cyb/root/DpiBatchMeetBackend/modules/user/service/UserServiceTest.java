package mh.cyb.root.DpiBatchMeetBackend.modules.user.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.repository.UserRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.RegisterRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.CreateUserRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserServiceImpl;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.mapper.UserMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.ApprovalService;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.ApprovalRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private ApprovalService approvalService;

        @Mock
        private UserMapper userMapper;

        @InjectMocks
        private UserServiceImpl userService;

        @BeforeEach
        public void setup() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        public void testRegisterUser() {
                RegisterRequest registerRequest = RegisterRequest
                                .builder()
                                .email("new@example.com")
                                .password("plainPassword")
                                .fullName("New User")
                                .build();

                User user = User.builder()
                                .id(1L)
                                .email("new@example.com")
                                .password("encodedPassword")
                                .fullName("New User")
                                .isEnabled(false)
                                .roles(Set.of(Role.MEMBER))
                                .build();

                UserDto userDto = UserDto
                                .builder()
                                .id(1L)
                                .email("new@example.com")
                                .fullName("New User")
                                .isEnabled(false)
                                .roles(Set.of(Role.MEMBER))
                                .build();

                when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

                when(userMapper.toEntity(any(RegisterRequest.class)))
                                .thenAnswer(invocation -> {
                                        return User.builder()
                                                        .email("new@example.com")
                                                        .fullName("New User")
                                                        .build();
                                });

                when(userRepository.save(any(User.class))).thenReturn(user);
                when(approvalService.createRequest(any(User.class)))
                                .thenReturn(new ApprovalRequestDto());
                when(userMapper.toDto(any(User.class))).thenReturn(userDto);

                UserDto registeredUser = userService.registerUser(registerRequest);

                assertNotNull(registeredUser.getId());
                assertEquals("new@example.com", registeredUser.getEmail());
                assertFalse(registeredUser.isEnabled());
                // assertEquals(Role.MEMBER, registeredUser.getRole()); // UserDto has Set<Role>
                // roles
                verify(userRepository, times(1)).save(any(User.class));
                verify(approvalService, times(1)).createRequest(any(User.class));
        }

        @Test
        public void testFindByEmail() {
                User user = new User();
                user.setEmail("found@example.com");
                when(userRepository.findByEmail("found@example.com")).thenReturn(Optional.of(user));

                Optional<User> found = userService.findByEmail("found@example.com");
                assertTrue(found.isPresent());
                assertEquals("found@example.com", found.get().getEmail());
        }
}
