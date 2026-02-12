package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.Memory;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.MemoryDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.UploadMemoryRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.mapper.MemoryMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.repository.MemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoryServiceImplTest {

    @Mock
    private MemoryRepository memoryRepository;
    @Mock
    private MemoryMapper memoryMapper;

    @InjectMocks
    private MemoryServiceImpl memoryService;

    private Memory memory;

    @BeforeEach
    void setUp() {
        memory = new Memory();
        memory.setId(1L);
        memory.setUploaderId(1L);
    }

    @Test
    void getAllMemories_ShouldReturnList() {
        when(memoryRepository.findAllByOrderByCreatedAtDesc()).thenReturn(java.util.List.of(memory));
        when(memoryMapper.toDto(any())).thenReturn(new MemoryDto());

        java.util.List<MemoryDto> result = memoryService.getAllMemories();

        assertThat(result).hasSize(1);
        verify(memoryRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void uploadMemory_ShouldCallRepository() {
        UploadMemoryRequest request = new UploadMemoryRequest();
        when(memoryMapper.toEntity(request)).thenReturn(memory);
        when(memoryRepository.save(any())).thenReturn(memory);
        when(memoryMapper.toDto(any())).thenReturn(new MemoryDto());

        memoryService.uploadMemory(request, 1L);

        verify(memoryRepository).save(any());
    }

    @Test
    void deleteMemory_ShouldThrowForUnauthorizedUser() {
        when(memoryRepository.findById(1L)).thenReturn(Optional.of(memory));

        assertThatThrownBy(() -> memoryService.deleteMemory(1L, 2L, false))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void deleteMemory_ShouldSucceedForUploader() {
        when(memoryRepository.findById(1L)).thenReturn(Optional.of(memory));

        memoryService.deleteMemory(1L, 1L, false);

        verify(memoryRepository).deleteById(1L);
    }
}
