package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.Notice;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.NoticeDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreateNoticeRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.repository.NoticeRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeRepository noticeRepository, NoticeMapper noticeMapper) {
        this.noticeRepository = noticeRepository;
        this.noticeMapper = noticeMapper;
    }

    @Override
    public List<NoticeDto> getAllActiveNotices() {
        return noticeRepository
                .findByExpiresAtAfterOrExpiresAtIsNullOrderByIsPinnedDescCreatedAtDesc(LocalDateTime.now())
                .stream()
                .map(noticeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public NoticeDto createNotice(CreateNoticeRequest request, Long authorId) {
        Notice notice = noticeMapper.toEntity(request);
        notice.setAuthorId(authorId);
        return noticeMapper.toDto(noticeRepository.save(notice));
    }

    @Override
    public NoticeDto updateNotice(Long id, CreateNoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        if (request.getTitle() != null) {
            notice.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            notice.setContent(request.getContent());
        }
        if (request.getIsPinned() != null) {
            notice.setIsPinned(request.getIsPinned());
        }
        if (request.getExpiresAt() != null) {
            notice.setExpiresAt(request.getExpiresAt());
        }

        return noticeMapper.toDto(noticeRepository.save(notice));
    }

    @Override
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }
}
