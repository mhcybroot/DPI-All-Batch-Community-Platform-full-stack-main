package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.NoticeDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreateNoticeRequest;
import java.util.List;

public interface NoticeService {
    List<NoticeDto> getAllActiveNotices();

    NoticeDto createNotice(CreateNoticeRequest request, Long authorId);

    NoticeDto updateNotice(Long id, CreateNoticeRequest request);

    void deleteNotice(Long id);
}
