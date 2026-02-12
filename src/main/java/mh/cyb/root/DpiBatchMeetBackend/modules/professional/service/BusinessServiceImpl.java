package mh.cyb.root.DpiBatchMeetBackend.modules.professional.service;

import lombok.RequiredArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BusinessProfile;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BusinessProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.mapper.BusinessMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.repository.BusinessProfileRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessServiceImpl implements BusinessService {

    private final BusinessProfileRepository businessProfileRepository;
    private final BusinessMapper businessMapper;

    @Override
    public BusinessProfileDto registerBusiness(CreateBusinessRequest request, User user) {
        BusinessProfile profile = businessMapper.toEntity(request);
        profile.setOwner(user);
        profile = businessProfileRepository.save(profile);
        return businessMapper.toDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BusinessProfileDto> getAllBusinesses(String search, Pageable pageable) {
        // TODO: Implement search
        return businessProfileRepository.findAll(pageable).map(businessMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessProfileDto getBusinessById(Long id) {
        BusinessProfile profile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business profile not found"));
        return businessMapper.toDto(profile);
    }

    @Override
    public BusinessProfileDto updateBusiness(Long id, UpdateBusinessRequest request, User user) {
        BusinessProfile profile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business profile not found"));

        if (!profile.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this business profile");
        }

        businessMapper.updateBusinessFromRequest(request, profile);
        profile = businessProfileRepository.save(profile);
        return businessMapper.toDto(profile);
    }

    @Override
    public void deleteBusiness(Long id, User user) {
        BusinessProfile profile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business profile not found"));

        if (!profile.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this business profile");
        }

        businessProfileRepository.delete(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BusinessProfileDto> getBusinessesByUser(User user, Pageable pageable) {
        return businessProfileRepository.findByOwner(user, pageable).map(businessMapper::toDto);
    }
}
