package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.*;
import java.util.List;

public interface ForumService {
    List<ForumCategoryDto> getAllCategories();

    List<ForumPostDto> getPostsByCategory(Long categoryId);

    List<ForumPostDto> getAllPosts();

    ForumPostDto createPost(CreatePostRequest request, Long authorId);

    List<ForumCommentDto> getCommentsByPost(Long postId);

    ForumCommentDto addComment(Long postId, CreateCommentRequest request, Long authorId);

    // Category CRUD
    ForumCategoryDto createCategory(CreateCategoryRequest request);

    ForumCategoryDto updateCategory(Long id, CreateCategoryRequest request);

    void deleteCategory(Long id);

    // Post CRUD
    ForumPostDto getPostById(Long id);

    ForumPostDto updatePost(Long id, UpdatePostRequest request, Long requesterId, boolean isAdmin);

    void deletePost(Long id, Long requesterId, boolean isAdmin);

    // Comment CRUD
    void deleteComment(Long id, Long requesterId, boolean isAdmin);
}
