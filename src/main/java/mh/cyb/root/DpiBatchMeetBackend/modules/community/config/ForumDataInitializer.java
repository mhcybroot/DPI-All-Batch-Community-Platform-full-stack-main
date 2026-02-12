package mh.cyb.root.DpiBatchMeetBackend.modules.community.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreateCategoryRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.ForumService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForumDataInitializer implements CommandLineRunner {

    private final ForumService forumService;

    @Override
    public void run(String... args) {
        if (forumService.getAllCategories().isEmpty()) {
            log.info("Initializing forum categories...");

            createCategory("🚀 Technology", "Discussions about software, hardware, and emerging tech.", "code");
            createCategory("💼 Careers", "Job hunting, interview prep, and professional growth.", "briefcase");
            createCategory("🎯 Projects", "Showcase what you're building and find collaborators.", "terminal");
            createCategory("☕ Random", "Off-topic chats, memes, and casual conversation.", "coffee");
            createCategory("📢 Announcements", "Official updates from the community leaders.", "megaphone");

            log.info("Forum categories initialized successfully.");
        }
    }

    private void createCategory(String name, String description, String icon) {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(name);
        request.setDescription(description);
        request.setIconUrl(icon);
        forumService.createCategory(request);
    }
}
