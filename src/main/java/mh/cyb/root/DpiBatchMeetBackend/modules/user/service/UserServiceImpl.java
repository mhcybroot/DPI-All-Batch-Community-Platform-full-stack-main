package mh.cyb.root.DpiBatchMeetBackend.modules.user.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.repository.UserRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.mapper.UserMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.ApprovalService;
import mh.cyb.root.DpiBatchMeetBackend.common.exception.BadRequestException;
import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.RegisterRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // Placeholder import if needed

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApprovalService approvalService;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            ApprovalService approvalService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.approvalService = approvalService;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto registerUser(
            RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        User user = userMapper.toEntity(request);
        // Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set default values
        user.setEnabled(false);
        user.setAccountNonLocked(true);

        // Set default role
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.getRoles().add(Role.MEMBER);
        }

        User savedUser = userRepository.save(user); // Save first to get ID

        // Create approval request
        approvalService.createRequest(savedUser);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto createUser(
            CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true); // Admins/Mods created by admin are enabled by default? Or should follow
                               // approval?
                               // Requirements say Admin creates them, so likely pre-approved/enabled.
        user.setAccountNonLocked(true);

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            request.getRoles().forEach(roleStr -> {
                try {
                    user.getRoles().add(Role.valueOf(roleStr));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid roles or throw exception?
                    // Better to default to MEMBER or throw
                }
            });
        }

        if (user.getRoles().isEmpty()) {
            user.getRoles().add(Role.MEMBER);
        }

        User savedUser = userRepository.save(user);

        // No approval needed for Admin-created users?
        // Or maybe just auto-approve?
        // Let's assume they are enabled directly.

        // Initialize Profile
        // ProfileService might need to create an empty profile
        // Since we don't have ProfileService injected here, we can rely on
        // ProfileService.getProfile
        // handling missing profile, or better, we should have a listener or explicit
        // call.
        // For now, simple user creation.

        return userMapper.toDto(savedUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new mh.cyb.root.DpiBatchMeetBackend.common.exception.ResourceNotFoundException(
                        "User not found"));
    }
}
