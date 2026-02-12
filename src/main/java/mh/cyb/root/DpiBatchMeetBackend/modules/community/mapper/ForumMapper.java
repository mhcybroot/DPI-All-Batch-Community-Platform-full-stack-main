package mh.cyb.root.DpiBatchMeetBackend.modules.community.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.ForumCategory;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.ForumPost;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.ForumComment;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ForumMapper {
    ForumCategoryDto toDto(ForumCategory category);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ForumPostDto toDto(ForumPost post);

    @Mapping(source = "post.id", target = "postId")
    ForumCommentDto toDto(ForumComment comment);

    @Mapping(target = "id", ignore = true)
    ForumCategory toEntity(CreateCategoryRequest request);
}
