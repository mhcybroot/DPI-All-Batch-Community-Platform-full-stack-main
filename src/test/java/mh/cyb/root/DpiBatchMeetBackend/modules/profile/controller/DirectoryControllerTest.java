package mh.cyb.root.DpiBatchMeetBackend.modules.profile.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.service.DirectoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DirectoryControllerTest {

    @Mock
    private DirectoryService directoryService;

    @InjectMocks
    private DirectoryController directoryController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearch() {
        when(directoryService.searchProfiles("query")).thenReturn(Collections.emptyList());
        var response = directoryController.search("query");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(directoryService, times(1)).searchProfiles("query");
    }
}
