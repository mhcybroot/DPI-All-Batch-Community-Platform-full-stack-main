package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String title;
    private String content;
    private Long categoryId;
}
