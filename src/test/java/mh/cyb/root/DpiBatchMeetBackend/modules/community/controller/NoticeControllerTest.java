package mh.cyb.root.DpiBatchMeetBackend.modules.community.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreateNoticeRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.NoticeDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.NoticeService;
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

class NoticeControllerTest {

    @Mock
    private NoticeService noticeService;
    @Mock
    private UserService userService;
    @InjectMocks
    private NoticeController noticeController;
    @Mock
    private UserDetails userDetails;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("admin@test.com");
    }

    @Test
    void getAllNotices_ShouldReturnOk() {
        when(noticeService.getAllActiveNotices()).thenReturn(Collections.emptyList());
        ResponseEntity<List<NoticeDto>> response = noticeController.getAllNotices();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createNotice_ShouldReturnCreated() {
        CreateNoticeRequest request = new CreateNoticeRequest();
        when(userDetails.getUsername()).thenReturn("admin@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(noticeService.createNotice(any(), anyLong())).thenReturn(new NoticeDto());

        ResponseEntity<NoticeDto> response = noticeController.createNotice(request, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(noticeService).createNotice(any(), eq(1L));
    }

    @Test
    void updateNotice_ShouldReturnOk() {
        CreateNoticeRequest request = new CreateNoticeRequest();
        when(noticeService.updateNotice(eq(1L), any())).thenReturn(new NoticeDto());

        ResponseEntity<NoticeDto> response = noticeController.updateNotice(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(noticeService).updateNotice(eq(1L), any());
    }

    @Test
    void deleteNotice_ShouldReturnNoContent() {
        doNothing().when(noticeService).deleteNotice(1L);

        ResponseEntity<Void> response = noticeController.deleteNotice(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(noticeService).deleteNotice(1L);
    }
}
