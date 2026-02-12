package mh.cyb.root.DpiBatchMeetBackend.modules.event.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Poll;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.PollOption;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.PollDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.PollOptionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PollMapper {
    @Mapping(source = "event.id", target = "eventId")
    PollDto toDto(Poll poll);

    PollOptionDto toDto(PollOption option);
}
