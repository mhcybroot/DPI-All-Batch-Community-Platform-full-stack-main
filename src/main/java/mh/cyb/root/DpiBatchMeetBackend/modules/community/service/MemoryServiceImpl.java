package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.Memory;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.MemoryDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.UploadMemoryRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.repository.MemoryRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.mapper.MemoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemoryServiceImpl implements MemoryService {

    private final MemoryRepository memoryRepository;
    private final MemoryMapper memoryMapper;

    public MemoryServiceImpl(MemoryRepository memoryRepository, MemoryMapper memoryMapper) {
        this.memoryRepository = memoryRepository;
        this.memoryMapper = memoryMapper;
    }

    @Override
    public List<MemoryDto> getAllMemories() {
        return memoryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(memoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MemoryDto uploadMemory(UploadMemoryRequest request, Long uploaderId) {
        Memory memory = memoryMapper.toEntity(request);
        memory.setUploaderId(uploaderId);
        return memoryMapper.toDto(memoryRepository.save(memory));
    }

    @Override
    public void deleteMemory(Long id, Long requesterId, boolean isAdmin) {
        Memory memory = memoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Memory not found"));
        if (!isAdmin && !memory.getUploaderId().equals(requesterId)) {
            throw new RuntimeException("Unauthorized to delete this memory");
        }
        memoryRepository.deleteById(id);
    }
}
