package mh.cyb.root.DpiBatchMeetBackend.modules.community.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.MemoryDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.UploadMemoryRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.MemoryService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MemoryControllerTest {

    @Mock
    private MemoryService memoryService;
    @Mock
    private UserService userService;
    @InjectMocks
    private MemoryController memoryController;
    @Mock
    private UserDetails userDetails;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
    }

    @Test
    void getAllMemories_ShouldReturnOk() {
        when(memoryService.getAllMemories()).thenReturn(Collections.emptyList());
        ResponseEntity<List<MemoryDto>> response = memoryController.getAllMemories();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void uploadMemory_ShouldReturnCreated() {
        UploadMemoryRequest request = new UploadMemoryRequest();
        when(userDetails.getUsername()).thenReturn("user@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(memoryService.uploadMemory(any(), anyLong())).thenReturn(new MemoryDto());

        ResponseEntity<MemoryDto> response = memoryController.uploadMemory(request, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(memoryService).uploadMemory(any(), eq(1L));
    }

    @Test
    void deleteMemory_ShouldReturnNoContent() {
        when(userDetails.getUsername()).thenReturn("user@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(memoryService).deleteMemory(anyLong(), anyLong(), anyBoolean());

        ResponseEntity<Void> response = memoryController.deleteMemory(1L, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
