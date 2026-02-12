package mh.cyb.root.DpiBatchMeetBackend.modules.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.ForumService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import mh.cyb.root.DpiBatchMeetBackend.common.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
@Tag(name = "Discussion Forum", description = "Endpoints for category-based discussion forum")
public class ForumController {

    private final ForumService forumService;
    private final UserService userService;

    public ForumController(ForumService forumService, UserService userService) {
        this.forumService = forumService;
        this.userService = userService;
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Retrieves all discussion forum categories.")
    public ResponseEntity<List<ForumCategoryDto>> getAllCategories() {
        return ResponseEntity.ok(forumService.getAllCategories());
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Create category", description = "Creates a new category. Admin only.")
    public ResponseEntity<ForumCategoryDto> createCategory(@RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(201).body(forumService.createCategory(request));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Update category", description = "Updates an existing category. Admin only.")
    public ResponseEntity<ForumCategoryDto> updateCategory(@PathVariable Long id,
            @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(forumService.updateCategory(id, request));
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Delete category", description = "Deletes a category. Admin only.")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        forumService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts")
    @Operation(summary = "Get posts", description = "Retrieves all posts, optionally filtered by category.")
    public ResponseEntity<List<ForumPostDto>> getPosts(@RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(forumService.getPostsByCategory(categoryId));
        }
        return ResponseEntity.ok(forumService.getAllPosts());
    }

    @PostMapping("/posts")
    @Operation(summary = "Create a post", description = "Creates a new forum post in a specific category.")
    public ResponseEntity<ForumPostDto> createPost(@RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.status(201).body(forumService.createPost(request, user.getId()));
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Get post by ID", description = "Retrieves a single forum post by its ID.")
    public ResponseEntity<ForumPostDto> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(forumService.getPostById(id));
    }

    @PutMapping("/posts/{id}")
    @Operation(summary = "Update post", description = "Updates an existing post. Only the author or an admin can update.")
    public ResponseEntity<ForumPostDto> updatePost(@PathVariable Long id,
            @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isAdmin = user.getRoles().contains(Role.ADMINISTRATOR);
        return ResponseEntity.ok(forumService.updatePost(id, request, user.getId(), isAdmin));
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Delete post", description = "Deletes a post. Only the author or an admin can delete.")
    public ResponseEntity<Void> deletePost(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isAdmin = user.getRoles().contains(Role.ADMINISTRATOR);
        forumService.deletePost(id, user.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "Get comments", description = "Retrieves all comments for a specific post.")
    public ResponseEntity<List<ForumCommentDto>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(forumService.getCommentsByPost(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "Add a comment", description = "Adds a comment to a specific post.")
    public ResponseEntity<ForumCommentDto> addComment(@PathVariable Long postId,
            @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.status(201).body(forumService.addComment(postId, request, user.getId()));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    @Operation(summary = "Delete comment", description = "Deletes a comment. Only the author or an admin can delete.")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isAdmin = user.getRoles().contains(Role.ADMINISTRATOR);
        forumService.deleteComment(commentId, user.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }
}
