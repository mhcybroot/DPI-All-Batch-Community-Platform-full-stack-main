package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.MemoryDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.UploadMemoryRequest;
import java.util.List;

public interface MemoryService {
    List<MemoryDto> getAllMemories();

    org.springframework.data.domain.Page<MemoryDto> getAllMemories(org.springframework.data.domain.Pageable pageable);

    MemoryDto uploadMemory(UploadMemoryRequest request, Long uploaderId);

    void deleteMemory(Long id, Long requesterId, boolean isAdmin);
}
