package mh.cyb.root.DpiBatchMeetBackend.modules.profile.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Skill;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.SkillDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skillDto);
}
