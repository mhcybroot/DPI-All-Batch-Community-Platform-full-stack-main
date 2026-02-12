package mh.cyb.root.DpiBatchMeetBackend.modules.event.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Poll;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.PollOption;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.PollDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.PollOptionDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PollMapper {
    @Mapping(source = "event.id", target = "eventId")
    PollDto toDto(Poll poll);

    PollOptionDto toDto(PollOption option);

    @AfterMapping
    default void calculateTotalVotes(@MappingTarget PollDto dto, Poll poll) {
        if (poll.getOptions() != null) {
            dto.setTotalVotes(poll.getOptions().stream()
                    .mapToInt(mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.PollOption::getVoteCount)
                    .sum());
        }
    }
}
