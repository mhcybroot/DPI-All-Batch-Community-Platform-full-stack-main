package mh.cyb.root.DpiBatchMeetBackend.modules.event.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.CreatePollRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.PollDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

import java.util.List;

public interface PollService {
    PollDto createPoll(CreatePollRequest request, User creator);

    void vote(Long pollId, Long optionId, User voter);

    void closePoll(Long pollId, User requester);

    PollDto getPollResults(Long pollId);

    List<PollDto> getEventPolls(Long eventId);

    List<PollDto> getActivePolls();
}
