package mh.cyb.root.DpiBatchMeetBackend.modules.professional.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BusinessProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusinessService {
    BusinessProfileDto registerBusiness(CreateBusinessRequest request, User user);

    Page<BusinessProfileDto> getAllBusinesses(String search, Pageable pageable);

    BusinessProfileDto getBusinessById(Long id);

    BusinessProfileDto updateBusiness(Long id, UpdateBusinessRequest request, User user);

    void deleteBusiness(Long id, User user);

    Page<BusinessProfileDto> getBusinessesByUser(User user, Pageable pageable);
}
