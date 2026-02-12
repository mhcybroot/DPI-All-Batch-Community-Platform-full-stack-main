package mh.cyb.root.DpiBatchMeetBackend.modules.admin.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.domain.ApprovalRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.repository.ApprovalRequestRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.repository.UserRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.ApprovalRequestDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.mapper.ApprovalRequestMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final UserRepository userRepository;
    private final ApprovalRequestMapper approvalRequestMapper;
    private final AuditService auditService;

    public ApprovalServiceImpl(ApprovalRequestRepository approvalRequestRepository, UserRepository userRepository,
            ApprovalRequestMapper approvalRequestMapper, AuditService auditService) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.userRepository = userRepository;
        this.approvalRequestMapper = approvalRequestMapper;
        this.auditService = auditService;
    }

    @Override
    public ApprovalRequestDto createRequest(User user) {
        ApprovalRequest request = new ApprovalRequest();
        request.setUser(user);
        request.setStatus(ApprovalRequest.ApprovalStatus.PENDING);
        return approvalRequestMapper.toDto(approvalRequestRepository.save(request));
    }

    @Override
    public List<ApprovalRequestDto> getPendingRequests() {
        return approvalRequestRepository.findByStatus(ApprovalRequest.ApprovalStatus.PENDING).stream()
                .map(approvalRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ApprovalRequestDto approveRequest(Long requestId, User reviewer) {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found")); // In real app, use custom exception

        request.setStatus(ApprovalRequest.ApprovalStatus.APPROVED);
        request.setReviewedBy(reviewer);

        // Enable user
        User user = request.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        // Log action
        auditService.logAction(reviewer.getId(), "APPROVE_USER", String.valueOf(user.getId()),
                "Approved request ID: " + requestId, null);

        return approvalRequestMapper.toDto(approvalRequestRepository.save(request));
    }

    @Override
    public ApprovalRequestDto rejectRequest(Long requestId, User reviewer,
            String reason) {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(ApprovalRequest.ApprovalStatus.REJECTED);
        request.setReviewedBy(reviewer);
        request.setRejectionReason(reason);

        // User remains disabled

        // Log action
        auditService.logAction(reviewer.getId(), "REJECT_USER", String.valueOf(request.getUser().getId()),
                "Rejected request ID: " + requestId + ". Reason: " + reason, null);

        return approvalRequestMapper.toDto(approvalRequestRepository.save(request));
    }
}
