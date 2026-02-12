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
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PollServiceImplTest {

    @Mock
    private PollRepository pollRepository;
    @Mock
    private PollOptionRepository pollOptionRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private PollMapper pollMapper;

    @InjectMocks
    private PollServiceImpl pollService;

    private User user;
    private Event event;
    private Poll poll;
    private PollOption option1;
    private PollOption option2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        event = Event.builder().id(1L).title("Test Event").build();

        poll = Poll.builder()
                .id(1L)
                .event(event)
                .question("Test Poll?")
                .createdBy(user.getId())
                .closed(false)
                .build();

        option1 = PollOption.builder().id(1L).poll(poll).optionText("Option 1").voteCount(0).build();
        option2 = PollOption.builder().id(2L).poll(poll).optionText("Option 2").voteCount(0).build();

        poll.setOptions(Arrays.asList(option1, option2));
    }

    @Test
    void createPoll_ShouldCreateAndSave() {
        CreatePollRequest request = new CreatePollRequest();
        request.setEventId(1L);
        request.setQuestion("Test Poll?");
        request.setOptions(Arrays.asList("Option 1", "Option 2"));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(pollRepository.save(any(Poll.class))).thenReturn(poll);
        when(pollMapper.toDto(any(Poll.class))).thenReturn(PollDto.builder().id(1L).build());

        PollDto result = pollService.createPoll(request, user);

        assertThat(result.getId()).isEqualTo(1L);
        verify(pollRepository).save(any(Poll.class));
    }

    @Test
    void vote_ShouldSucceed_WhenNotVoted() {
        when(pollOptionRepository.findById(1L)).thenReturn(Optional.of(option1));
        when(voteRepository.findByPollIdAndVoterId(any(), eq(user.getId()))).thenReturn(Optional.empty());
        when(voteRepository.save(any(Vote.class))).thenReturn(new Vote());

        pollService.vote(1L, 1L, user);

        assertThat(option1.getVoteCount()).isEqualTo(1);
        verify(voteRepository).save(any(Vote.class));
        verify(pollOptionRepository).save(option1);
    }

    @Test
    void vote_ShouldThrow_WhenAlreadyVoted() {
        when(pollOptionRepository.findById(1L)).thenReturn(Optional.of(option1));
        when(voteRepository.findByPollIdAndVoterId(any(), eq(user.getId()))).thenReturn(Optional.of(new Vote()));

        assertThatThrownBy(() -> pollService.vote(1L, 1L, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already voted");
    }

    @Test
    void closePoll_ShouldSetIsClosedTrue() {
        when(pollRepository.findById(1L)).thenReturn(Optional.of(poll));
        when(pollRepository.save(any(Poll.class))).thenReturn(poll);

        pollService.closePoll(1L, user);

        assertThat(poll.isClosed()).isTrue();
    }

    @Test
    void vote_ShouldThrow_WhenOptionDoesNotBelongToPoll() {
        Poll otherPoll = Poll.builder().id(2L).build();
        option1.setPoll(otherPoll);
        when(pollOptionRepository.findById(1L)).thenReturn(Optional.of(option1));

        assertThatThrownBy(() -> pollService.vote(1L, 1L, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("does not belong to the specified poll");
    }

    @Test
    void vote_ShouldThrow_WhenPollIsClosed() {
        poll.setClosed(true);
        when(pollOptionRepository.findById(1L)).thenReturn(Optional.of(option1));

        assertThatThrownBy(() -> pollService.vote(1L, 1L, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("is closed");
    }

    @Test
    void vote_ShouldThrow_WhenDeadlineHasPassed() {
        poll.setDeadline(java.time.LocalDateTime.now().minusHours(1));
        when(pollOptionRepository.findById(1L)).thenReturn(Optional.of(option1));

        assertThatThrownBy(() -> pollService.vote(1L, 1L, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("deadline has passed");
    }

    @Test
    void closePoll_ShouldThrow_WhenUserIsNotCreatorOrAdmin() {
        User otherUser = new User();
        otherUser.setId(99L);
        otherUser.setRoles(new java.util.HashSet<>(
                Collections.singletonList(mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role.MEMBER)));

        when(pollRepository.findById(1L)).thenReturn(Optional.of(poll));

        assertThatThrownBy(() -> pollService.closePoll(1L, otherUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only the creator or an administrator");
    }

    @Test
    void getPollResults_ShouldReturnPollWithCounts() {
        when(pollRepository.findById(1L)).thenReturn(Optional.of(poll));
        when(pollMapper.toDto(poll)).thenReturn(PollDto.builder().id(1L).build());

        PollDto result = pollService.getPollResults(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getEventPolls_ShouldReturnList() {
        when(pollRepository.findByEventId(1L)).thenReturn(List.of(poll));
        when(pollMapper.toDto(poll)).thenReturn(PollDto.builder().id(1L).build());

        List<PollDto> result = pollService.getEventPolls(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getActivePolls_ShouldReturnList() {
        when(pollRepository.findByClosedFalse()).thenReturn(List.of(poll));
        when(pollMapper.toDto(poll)).thenReturn(PollDto.builder().id(1L).build());

        List<PollDto> result = pollService.getActivePolls();

        assertThat(result).hasSize(1);
    }
}
