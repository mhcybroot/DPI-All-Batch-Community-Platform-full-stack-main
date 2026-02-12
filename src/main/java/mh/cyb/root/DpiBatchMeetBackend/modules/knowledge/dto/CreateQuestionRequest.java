package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateQuestionRequest {
    private String title;
    private String body;
    private List<String> tags;
}
