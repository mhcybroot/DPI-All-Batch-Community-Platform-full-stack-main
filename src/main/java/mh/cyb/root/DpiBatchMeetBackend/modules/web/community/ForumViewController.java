package mh.cyb.root.DpiBatchMeetBackend.modules.web.community;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreateCategoryRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreateCommentRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreatePostRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.UpdatePostRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.ForumService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/forum")
public class ForumViewController {

    private final ForumService forumService;
    private final UserService userService;

    public ForumViewController(ForumService forumService, UserService userService) {
        this.forumService = forumService;
        this.userService = userService;
    }

    @GetMapping("")
    public String categories(Model model) {
        model.addAttribute("categories", forumService.getAllCategories());
        model.addAttribute("activeNav", "forum");
        return "forum/index";
    }

    @GetMapping("/category/{id}")
    public String categoryPosts(@PathVariable Long id, Model model) {
        model.addAttribute("posts", forumService.getPostsByCategory(id));
        model.addAttribute("categoryId", id);
        model.addAttribute("activeNav", "forum");
        return "forum/category";
    }

    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("post", forumService.getPostById(id));
        model.addAttribute("comments", forumService.getCommentsByPost(id));
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("isAdmin", user.getRoles().contains(Role.ADMINISTRATOR));
        model.addAttribute("activeNav", "forum");
        return "forum/post";
    }

    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        model.addAttribute("categories", forumService.getAllCategories());
        model.addAttribute("activeNav", "forum");
        return "forum/new-post";
    }

    @PostMapping("/posts")
    public String createPost(@RequestParam String title,
            @RequestParam String content,
            @RequestParam Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setCategoryId(categoryId);
        forumService.createPost(request, user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Post created!");
        return "redirect:/web/forum/category/" + categoryId;
    }

    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable Long postId,
            @RequestParam String content,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent(content);
        forumService.addComment(postId, request, user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Comment added!");
        return "redirect:/web/forum/posts/" + postId;
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        boolean isAdmin = user.getRoles().contains(Role.ADMINISTRATOR);
        forumService.deletePost(id, user.getId(), isAdmin);
        redirectAttributes.addFlashAttribute("successMessage", "Post deleted.");
        return "redirect:/web/forum";
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id,
            @RequestParam Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        boolean isAdmin = user.getRoles().contains(Role.ADMINISTRATOR);
        forumService.deleteComment(id, user.getId(), isAdmin);
        redirectAttributes.addFlashAttribute("successMessage", "Comment deleted.");
        return "redirect:/web/forum/posts/" + postId;
    }

    // --- Phase D: Category CRUD + Edit Post ---

    @GetMapping("/categories/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("activeNav", "forum");
        return "forum/new-category";
    }

    @PostMapping("/categories")
    public String createCategory(@RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String iconUrl,
            RedirectAttributes redirectAttributes) {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(name);
        request.setDescription(description);
        request.setIconUrl(iconUrl);
        forumService.createCategory(request);
        redirectAttributes.addFlashAttribute("successMessage", "Category created!");
        return "redirect:/web/forum";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        forumService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category deleted.");
        return "redirect:/web/forum";
    }

    @GetMapping("/posts/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model) {
        model.addAttribute("post", forumService.getPostById(id));
        model.addAttribute("activeNav", "forum");
        return "forum/edit-post";
    }

    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        boolean isAdmin = user.getRoles().contains(Role.ADMINISTRATOR);
        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle(title);
        request.setContent(content);
        forumService.updatePost(id, request, user.getId(), isAdmin);
        redirectAttributes.addFlashAttribute("successMessage", "Post updated!");
        return "redirect:/web/forum/posts/" + id;
    }

    @GetMapping("/categories/{id}/edit")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", forumService.getCategoryById(id));
        model.addAttribute("activeNav", "forum");
        return "forum/edit-category";
    }

    @PostMapping("/categories/{id}/edit")
    public String updateCategory(@PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String iconUrl,
            RedirectAttributes redirectAttributes) {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(name);
        request.setDescription(description);
        request.setIconUrl(iconUrl);
        forumService.updateCategory(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Category updated!");
        return "redirect:/web/forum";
    }
}
