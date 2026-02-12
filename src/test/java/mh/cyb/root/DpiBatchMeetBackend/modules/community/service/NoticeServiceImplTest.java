package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.Notice;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreateNoticeRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.NoticeDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.mapper.NoticeMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.repository.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private NoticeMapper noticeMapper;

    @InjectMocks
    private NoticeServiceImpl noticeService;

    private Notice notice;
    private NoticeDto noticeDto;
    private CreateNoticeRequest createRequest;

    @BeforeEach
    void setUp() {
        notice = Notice.builder()
                .id(1L)
                .title("Test Notice")
                .content("Test Content")
                .isPinned(false)
                .authorId(1L)
                .build();

        noticeDto = new NoticeDto();
        noticeDto.setId(1L);
        noticeDto.setTitle("Test Notice");
        noticeDto.setContent("Test Content");

        createRequest = new CreateNoticeRequest();
        createRequest.setTitle("New Notice");
        createRequest.setContent("New Content");
    }

    @Test
    void getAllActiveNotices_ShouldReturnList() {
        when(noticeRepository
                .findByExpiresAtAfterOrExpiresAtIsNullOrderByIsPinnedDescCreatedAtDesc(any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(notice));
        when(noticeMapper.toDto(notice)).thenReturn(noticeDto);

        List<NoticeDto> result = noticeService.getAllActiveNotices();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(noticeDto.getTitle());
    }

    @Test
    void createNotice_ShouldReturnCreatedNotice() {
        when(noticeMapper.toEntity(createRequest)).thenReturn(notice);
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);
        when(noticeMapper.toDto(notice)).thenReturn(noticeDto);

        NoticeDto result = noticeService.createNotice(createRequest, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(noticeDto.getId());
        verify(noticeRepository).save(any(Notice.class));
    }

    @Test
    void updateNotice_ShouldOnlyUpdateProvidedFields() {
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        createRequest.setTitle("Updated Title");
        createRequest.setContent(null); // Should not update content

        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);
        when(noticeMapper.toDto(notice)).thenReturn(noticeDto);

        noticeService.updateNotice(1L, createRequest);

        assertThat(notice.getTitle()).isEqualTo("Updated Title");
        assertThat(notice.getContent()).isEqualTo("Test Content");
        verify(noticeRepository).save(notice);
    }

    @Test
    void deleteNotice_ShouldCallRepository() {
        noticeService.deleteNotice(1L);
        verify(noticeRepository).deleteById(1L);
    }
}
