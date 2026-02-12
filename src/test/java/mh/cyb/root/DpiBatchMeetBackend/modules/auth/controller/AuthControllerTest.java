package mh.cyb.root.DpiBatchMeetBackend.modules.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.AuditService;
import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.AuthResponse;
import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.LoginRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.RegisterRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import mh.cyb.root.DpiBatchMeetBackend.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private UserService userService;
    @Mock
    private AuditService auditService;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldReturnOk() {
        RegisterRequest registerRequest = RegisterRequest.builder().email("test@test.com").build();
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.registerUser(any())).thenReturn(new UserDto());

        ResponseEntity<?> response = authController.registerUser(registerRequest, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService).registerUser(any());
        verify(auditService, times(1)).logAction(eq(null), eq("USER_REGISTRATION"), any(), any(), any());
    }

    @Test
    void loginUser_ShouldReturnOk() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
        when(tokenProvider.generateToken(anyString())).thenReturn("mockToken");
        when(userService.findByEmail(anyString()))
                .thenReturn(Optional.of(new mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User()));

        ResponseEntity<?> response = authController.loginUser(loginRequest, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse body = (AuthResponse) response.getBody();
        assertEquals("mockToken", body.getAccessToken());
        verify(auditService, times(1)).logAction(any(), eq("USER_LOGIN"), any(), any(), any());
    }
}
