package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.Data;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.MediaType;

@Data
public class UploadMemoryRequest {
    private String title;
    private String description;
    private String mediaUrl;
    private MediaType mediaType;
}
