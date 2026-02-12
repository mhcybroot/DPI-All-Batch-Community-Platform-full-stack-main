package mh.cyb.root.DpiBatchMeetBackend.modules.professional.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BusinessProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.service.BusinessService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class BusinessControllerTest {

    @Mock
    private BusinessService businessService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private BusinessController businessController;

    private User user;
    private BusinessProfileDto profileDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        profileDto = new BusinessProfileDto();
        profileDto.setId(1L);
        profileDto.setBusinessName("Test Corp");
    }

    @Test
    void registerBusiness_ShouldReturnCreated() {
        CreateBusinessRequest request = new CreateBusinessRequest();
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(businessService.registerBusiness(any(), any())).thenReturn(profileDto);

        ResponseEntity<BusinessProfileDto> response = businessController.registerBusiness(userDetails, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(profileDto, response.getBody());
    }

    @Test
    void getAllBusinesses_ShouldReturnOk() {
        Page<BusinessProfileDto> page = new PageImpl<>(Collections.singletonList(profileDto));
        when(businessService.getAllBusinesses(any(), any())).thenReturn(page);

        ResponseEntity<Page<BusinessProfileDto>> response = businessController.getAllBusinesses(null,
                Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getBusinessById_ShouldReturnOk() {
        when(businessService.getBusinessById(1L)).thenReturn(profileDto);

        ResponseEntity<BusinessProfileDto> response = businessController.getBusinessById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profileDto, response.getBody());
    }

    @Test
    void updateBusiness_ShouldReturnOk() {
        UpdateBusinessRequest request = new UpdateBusinessRequest();
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(businessService.updateBusiness(eq(1L), any(), any())).thenReturn(profileDto);

        ResponseEntity<BusinessProfileDto> response = businessController.updateBusiness(userDetails, 1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteBusiness_ShouldReturnNoContent() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        doNothing().when(businessService).deleteBusiness(eq(1L), any());

        ResponseEntity<Void> response = businessController.deleteBusiness(userDetails, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
