package mh.cyb.root.DpiBatchMeetBackend.modules.community.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.ForumService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong; // Added this import
import static org.mockito.Mockito.*;

class ForumControllerTest {

    @Mock
    private ForumService forumService;
    @Mock
    private UserService userService;
    @InjectMocks
    private ForumController forumController;
    @Mock
    private UserDetails userDetails;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
    }

    @Test
    void getAllCategories_ShouldReturnOk() {
        when(forumService.getAllCategories()).thenReturn(Collections.emptyList());
        ResponseEntity<List<ForumCategoryDto>> response = forumController.getAllCategories();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createPost_ShouldReturnCreated() {
        CreatePostRequest request = new CreatePostRequest();
        when(userDetails.getUsername()).thenReturn("user@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(forumService.createPost(any(), anyLong())).thenReturn(new ForumPostDto());

        ResponseEntity<ForumPostDto> response = forumController.createPost(request, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createCategory_ShouldReturnCreated() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        when(forumService.createCategory(any())).thenReturn(new ForumCategoryDto());

        ResponseEntity<ForumCategoryDto> response = forumController.createCategory(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void updateCategory_ShouldReturnOk() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        when(forumService.updateCategory(eq(1L), any())).thenReturn(new ForumCategoryDto());

        ResponseEntity<ForumCategoryDto> response = forumController.updateCategory(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteCategory_ShouldReturnNoContent() {
        doNothing().when(forumService).deleteCategory(1L);

        ResponseEntity<Void> response = forumController.deleteCategory(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getPosts_ShouldReturnOk() {
        when(forumService.getAllPosts()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ForumPostDto>> response = forumController.getPosts(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getPost_ShouldReturnOk() {
        when(forumService.getPostById(1L)).thenReturn(new ForumPostDto());

        ResponseEntity<ForumPostDto> response = forumController.getPost(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updatePost_ShouldReturnOk() {
        UpdatePostRequest request = new UpdatePostRequest();
        when(userDetails.getUsername()).thenReturn("user@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(forumService.updatePost(anyLong(), any(), anyLong(), anyBoolean())).thenReturn(new ForumPostDto());

        ResponseEntity<ForumPostDto> response = forumController.updatePost(1L, request, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deletePost_ShouldReturnNoContent() {
        when(userDetails.getUsername()).thenReturn("user@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(forumService).deletePost(anyLong(), anyLong(), anyBoolean());

        ResponseEntity<Void> response = forumController.deletePost(1L, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getComments_ShouldReturnOk() {
        when(forumService.getCommentsByPost(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ForumCommentDto>> response = forumController.getComments(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addComment_ShouldReturnCreated() {
        CreateCommentRequest request = new CreateCommentRequest();
        when(userDetails.getUsername()).thenReturn("user@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(forumService.addComment(anyLong(), any(), anyLong())).thenReturn(new ForumCommentDto());

        ResponseEntity<ForumCommentDto> response = forumController.addComment(1L, request, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void deleteComment_ShouldReturnNoContent() {
        when(userDetails.getUsername()).thenReturn("user@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(forumService).deleteComment(anyLong(), anyLong(), anyBoolean());

        ResponseEntity<Void> response = forumController.deleteComment(1L, 1L, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
