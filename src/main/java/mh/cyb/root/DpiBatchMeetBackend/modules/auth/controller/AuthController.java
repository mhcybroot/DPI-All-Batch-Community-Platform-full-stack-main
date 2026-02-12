package mh.cyb.root.DpiBatchMeetBackend.modules.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.common.exception.BadRequestException;
import mh.cyb.root.DpiBatchMeetBackend.security.JwtTokenProvider;
import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.RegisterRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.LoginRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.AuthResponse;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for User Registration and Login")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final AuditService auditService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
            UserService userService, AuditService auditService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.auditService = auditService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with default MEMBER role and PENDING approval status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Email already in use")
    })
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Error: Email is already in use!");
        }
        UserDto registeredUser = userService.registerUser(request);
        auditService.logAction(null, "USER_REGISTRATION", String.valueOf(registeredUser.getId()),
                "User registered with email: " + registeredUser.getEmail(), httpRequest.getRemoteAddr());
        return ResponseEntity.status(201).body(registeredUser);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletRequest httpRequest) {
        org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        String jwt = tokenProvider.generateToken(authentication.getName());

        // Log login success
        User user = userService.findByEmail(loginRequest.getEmail()).orElse(null);
        if (user != null) {
            auditService.logAction(user.getId(), "USER_LOGIN", String.valueOf(user.getId()),
                    "User logged in successfully", httpRequest.getRemoteAddr());
        }

        return ResponseEntity.ok(new AuthResponse(jwt, "Bearer"));
    }
}
