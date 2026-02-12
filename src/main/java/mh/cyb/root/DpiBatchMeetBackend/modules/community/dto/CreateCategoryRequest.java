package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.Data;

@Data
public class CreateCategoryRequest {
    private String name;
    private String description;
    private String iconUrl;
}
