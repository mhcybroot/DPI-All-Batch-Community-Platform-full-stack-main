package mh.cyb.root.DpiBatchMeetBackend.modules.admin.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.domain.ApprovalRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.ApprovalService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.CreateUserRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.ApprovalRequestDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private ApprovalService approvalService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AdminController adminController;

    private User reviewer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        reviewer = new User();
        reviewer.setId(1L);
        reviewer.setEmail("admin@test.com");
    }

    @Test
    public void testGetPendingApprovals() {
        when(approvalService.getPendingRequests()).thenReturn(Collections.emptyList());
        ResponseEntity<List<ApprovalRequestDto>> response = adminController
                .getPendingApprovals();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(approvalService, times(1)).getPendingRequests();
    }

    @Test
    public void testCreateUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("mod@example.com");
        request.setFullName("Moderator");
        request.setPassword("password");
        request.setRoles(java.util.Set.of("MODERATOR"));

        UserDto userDto = new UserDto();
        userDto.setEmail("mod@example.com");

        when(userService.createUser(any(CreateUserRequest.class)))
                .thenReturn(userDto);

        ResponseEntity<UserDto> response = adminController.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("mod@example.com", response.getBody().getEmail());
        verify(userService, times(1)).createUser(request);
    }

    @Test
    void approveUser_ShouldReturnOk() {
        when(userDetails.getUsername()).thenReturn("admin@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(reviewer));
        when(approvalService.approveRequest(anyLong(), any())).thenReturn(new ApprovalRequestDto());

        ResponseEntity<?> response = adminController.approveUser(1L, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void rejectUser_ShouldReturnOk() {
        when(userDetails.getUsername()).thenReturn("admin@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(reviewer));
        when(approvalService.rejectRequest(anyLong(), any(), any())).thenReturn(new ApprovalRequestDto());

        ResponseEntity<?> response = adminController.rejectUser(1L, userDetails, "Reason");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
