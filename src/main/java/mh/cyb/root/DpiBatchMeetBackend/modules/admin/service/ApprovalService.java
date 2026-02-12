package mh.cyb.root.DpiBatchMeetBackend.modules.admin.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.ApprovalRequestDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import java.util.List;

public interface ApprovalService {
    ApprovalRequestDto createRequest(User user);

    List<ApprovalRequestDto> getPendingRequests();

    ApprovalRequestDto approveRequest(Long requestId, User reviewer);

    ApprovalRequestDto rejectRequest(Long requestId, User reviewer, String reason);
}
