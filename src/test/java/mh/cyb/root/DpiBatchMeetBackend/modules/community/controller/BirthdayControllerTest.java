package mh.cyb.root.DpiBatchMeetBackend.modules.community.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.BirthdayAlertDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.BirthdayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class BirthdayControllerTest {

    @Mock
    private BirthdayService birthdayService;
    @InjectMocks
    private BirthdayController birthdayController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTodayBirthdays_ShouldReturnOk() {
        when(birthdayService.getTodayBirthdays()).thenReturn(Collections.emptyList());
        ResponseEntity<List<BirthdayAlertDto>> response = birthdayController.getTodayBirthdays();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getUpcomingBirthdays_ShouldReturnOk() {
        when(birthdayService.getUpcomingBirthdays(anyInt())).thenReturn(Collections.emptyList());
        ResponseEntity<List<BirthdayAlertDto>> response = birthdayController.getUpcomingBirthdays(7);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
