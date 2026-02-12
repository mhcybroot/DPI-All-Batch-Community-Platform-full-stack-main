package mh.cyb.root.DpiBatchMeetBackend.modules.event.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Event;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Poll;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.PollOption;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Vote;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.CreatePollRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.PollDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.mapper.PollMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.EventRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.PollOptionRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.PollRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.VoteRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final VoteRepository voteRepository;
    private final EventRepository eventRepository;
    private final PollMapper pollMapper;

    public PollServiceImpl(PollRepository pollRepository, PollOptionRepository pollOptionRepository,
            VoteRepository voteRepository, EventRepository eventRepository, PollMapper pollMapper) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.voteRepository = voteRepository;
        this.eventRepository = eventRepository;
        this.pollMapper = pollMapper;
    }

    @Override
    @Transactional
    public PollDto createPoll(CreatePollRequest request, User creator) {
        Event event = null;
        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new RuntimeException("Event not found"));
        }

        Poll poll = Poll.builder()
                .event(event)
                .question(request.getQuestion())
                .createdBy(creator.getId())
                .multipleChoice(request.isMultipleChoice())
                .anonymous(request.isAnonymous())
                .deadline(request.getDeadline())
                .closed(false)
                .build();

        for (String text : request.getOptions()) {
            poll.addOption(PollOption.builder()
                    .optionText(text)
                    .voteCount(0)
                    .build());
        }

        return pollMapper.toDto(pollRepository.save(poll));
    }

    @Override
    @Transactional
    public void vote(Long pollId, Long optionId, User voter) {
        PollOption option = pollOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        // Validate option belongs to the specified poll
        if (!option.getPoll().getId().equals(pollId)) {
            throw new RuntimeException("Option does not belong to the specified poll");
        }

        if (option.getPoll().isClosed()) {
            throw new RuntimeException("This poll is closed");
        }

        if (option.getPoll().getDeadline() != null && option.getPoll().getDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Poll deadline has passed");
        }

        voteRepository.findByPollIdAndVoterId(option.getPoll().getId(), voter.getId()).ifPresent(v -> {
            throw new RuntimeException("You have already voted in this poll");
        });

        Vote vote = Vote.builder()
                .pollOption(option)
                .voterId(voter.getId())
                .build();

        voteRepository.save(vote);
        option.setVoteCount(option.getVoteCount() + 1);
        pollOptionRepository.save(option);
    }

    @Override
    @Transactional
    public void closePoll(Long pollId, User requester) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found"));

        boolean isAdmin = requester.getRoles().contains(Role.ADMINISTRATOR);
        boolean isCreator = poll.getCreatedBy().equals(requester.getId());

        if (!isAdmin && !isCreator) {
            throw new RuntimeException("Only the creator or an administrator can close this poll");
        }

        poll.setClosed(true);
        pollRepository.save(poll);
    }

    @Override
    public PollDto getPollResults(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found"));
        return pollMapper.toDto(poll);
    }

    @Override
    public List<PollDto> getEventPolls(Long eventId) {
        return pollRepository.findByEventId(eventId).stream()
                .map(pollMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<PollDto> getActivePolls() {
        return pollRepository.findByClosedFalse().stream()
                .map(pollMapper::toDto).collect(Collectors.toList());
    }
}
