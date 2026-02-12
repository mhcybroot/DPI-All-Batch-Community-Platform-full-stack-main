package mh.cyb.root.DpiBatchMeetBackend.modules.professional.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BusinessProfile;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BusinessProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.mapper.BusinessMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.repository.BusinessProfileRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessServiceImplTest {

    @Mock
    private BusinessProfileRepository businessProfileRepository;

    @Mock
    private BusinessMapper businessMapper;

    @InjectMocks
    private BusinessServiceImpl businessService;

    private User user;
    private BusinessProfile businessProfile;
    private BusinessProfileDto businessProfileDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);

        businessProfile = new BusinessProfile();
        businessProfile.setId(1L);
        businessProfile.setOwner(user);

        businessProfileDto = new BusinessProfileDto();
        businessProfileDto.setId(1L);
    }

    @Test
    void registerBusiness_ShouldCreateProfile() {
        CreateBusinessRequest request = new CreateBusinessRequest();
        when(businessMapper.toEntity(request)).thenReturn(businessProfile);
        when(businessProfileRepository.save(any())).thenReturn(businessProfile);
        when(businessMapper.toDto(businessProfile)).thenReturn(businessProfileDto);

        BusinessProfileDto result = businessService.registerBusiness(request, user);

        assertNotNull(result);
        verify(businessProfileRepository).save(any());
    }

    @Test
    void getAllBusinesses_ShouldReturnPage() {
        Page<BusinessProfile> page = new PageImpl<>(Collections.singletonList(businessProfile));
        when(businessProfileRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(businessMapper.toDto(businessProfile)).thenReturn(businessProfileDto);

        Page<BusinessProfileDto> result = businessService.getAllBusinesses(null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getBusinessById_ShouldReturnDto() {
        when(businessProfileRepository.findById(1L)).thenReturn(Optional.of(businessProfile));
        when(businessMapper.toDto(businessProfile)).thenReturn(businessProfileDto);

        BusinessProfileDto result = businessService.getBusinessById(1L);

        assertNotNull(result);
    }

    @Test
    void updateBusiness_ShouldUpdateProfile() {
        UpdateBusinessRequest request = new UpdateBusinessRequest();
        when(businessProfileRepository.findById(1L)).thenReturn(Optional.of(businessProfile));
        doNothing().when(businessMapper).updateBusinessFromRequest(request, businessProfile);
        when(businessProfileRepository.save(businessProfile)).thenReturn(businessProfile);
        when(businessMapper.toDto(businessProfile)).thenReturn(businessProfileDto);

        BusinessProfileDto result = businessService.updateBusiness(1L, request, user);

        assertNotNull(result);
        verify(businessProfileRepository).save(businessProfile);
    }

    @Test
    void deleteBusiness_ShouldDeleteProfile() {
        when(businessProfileRepository.findById(1L)).thenReturn(Optional.of(businessProfile));

        businessService.deleteBusiness(1L, user);

        verify(businessProfileRepository).delete(businessProfile);
    }
}
