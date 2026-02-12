package mh.cyb.root.DpiBatchMeetBackend.modules.user.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import java.util.Optional;

import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.RegisterRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.CreateUserRequest;

public interface UserService {
    UserDto registerUser(RegisterRequest request);

    UserDto createUser(CreateUserRequest request);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User getUserById(Long id);
}
