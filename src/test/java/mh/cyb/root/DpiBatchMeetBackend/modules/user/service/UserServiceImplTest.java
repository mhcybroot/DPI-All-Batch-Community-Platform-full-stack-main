package mh.cyb.root.DpiBatchMeetBackend.modules.user.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.RegisterRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.CreateUserRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.mapper.UserMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.repository.UserRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.ApprovalService;
import mh.cyb.root.DpiBatchMeetBackend.common.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

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

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRoles(new java.util.HashSet<>(Collections.singletonList(Role.MEMBER)));

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
    }

    @Test
    void registerUser_ShouldCreateUserAndApprovalRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.registerUser(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(approvalService).createRequest(user);
        verify(userRepository).save(user);
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email is already in use");
    }

    @Test
    void createUser_ShouldCreateEnabledUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setRoles(Set.of("ADMINISTRATOR"));

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(request);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenUserExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean exists = userService.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(mh.cyb.root.DpiBatchMeetBackend.common.exception.ResourceNotFoundException.class)
                .hasMessage("User not found");
    }
}
