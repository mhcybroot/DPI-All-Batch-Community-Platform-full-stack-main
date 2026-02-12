package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.mapper.ForumMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForumServiceImplTest {

    @Mock
    private ForumCategoryRepository categoryRepository;
    @Mock
    private ForumPostRepository postRepository;
    @Mock
    private ForumCommentRepository commentRepository;
    @Mock
    private ForumMapper forumMapper;

    @InjectMocks
    private ForumServiceImpl forumService;

    private ForumCategory category;
    private ForumPost post;
    private ForumComment comment;

    @BeforeEach
    void setUp() {
        category = ForumCategory.builder().id(1L).name("Tech").build();
        post = ForumPost.builder().id(1L).title("Java").authorId(1L).category(category).build();
        comment = ForumComment.builder().id(1L).content("Nice").authorId(1L).post(post).build();
    }

    @Test
    void getAllCategories_ShouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(forumMapper.toDto(category)).thenReturn(new ForumCategoryDto());

        List<ForumCategoryDto> result = forumService.getAllCategories();

        assertThat(result).hasSize(1);
    }

    @Test
    void getPostsByCategory_ShouldReturnFilteredPosts() {
        when(postRepository.findByCategoryIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(post));
        when(forumMapper.toDto(post)).thenReturn(new ForumPostDto());

        List<ForumPostDto> result = forumService.getPostsByCategory(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllPosts_ShouldReturnAllPosts() {
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post));
        when(forumMapper.toDto(post)).thenReturn(new ForumPostDto());

        List<ForumPostDto> result = forumService.getAllPosts();

        assertThat(result).hasSize(1);
    }

    @Test
    void createPost_ShouldReturnCreatedPost() {
        CreatePostRequest request = new CreatePostRequest();
        request.setCategoryId(1L);
        request.setTitle("Java");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(postRepository.save(any(ForumPost.class))).thenReturn(post);
        when(forumMapper.toDto(post)).thenReturn(new ForumPostDto());

        forumService.createPost(request, 1L);

        verify(postRepository).save(any(ForumPost.class));
    }

    @Test
    void getCommentsByPost_ShouldReturnComments() {
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(comment));
        when(forumMapper.toDto(comment)).thenReturn(new ForumCommentDto());

        List<ForumCommentDto> result = forumService.getCommentsByPost(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void addComment_ShouldReturnCreatedComment() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Nice");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(ForumComment.class))).thenReturn(comment);
        when(forumMapper.toDto(comment)).thenReturn(new ForumCommentDto());

        forumService.addComment(1L, request, 1L);

        verify(commentRepository).save(any(ForumComment.class));
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Tech");

        when(forumMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(any(ForumCategory.class))).thenReturn(category);
        when(forumMapper.toDto(category)).thenReturn(new ForumCategoryDto());

        forumService.createCategory(request);

        verify(categoryRepository).save(any(ForumCategory.class));
    }

    @Test
    void updateCategory_ShouldUpdateFields() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Updated");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(ForumCategory.class))).thenReturn(category);
        when(forumMapper.toDto(category)).thenReturn(new ForumCategoryDto());

        forumService.updateCategory(1L, request);

        assertThat(category.getName()).isEqualTo("Updated");
    }

    @Test
    void deleteCategory_ShouldCallRepository() {
        forumService.deleteCategory(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void getPostById_ShouldReturnPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(forumMapper.toDto(post)).thenReturn(new ForumPostDto());

        ForumPostDto result = forumService.getPostById(1L);

        assertThat(result).isNotNull();
        verify(postRepository).findById(1L);
    }

    @Test
    void updatePost_ShouldSucceedForAuthor() {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle("Updated");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(ForumPost.class))).thenReturn(post);
        when(forumMapper.toDto(any(ForumPost.class))).thenReturn(new ForumPostDto());

        forumService.updatePost(1L, request, 1L, false);

        assertThat(post.getTitle()).isEqualTo("Updated");
    }

    @Test
    void updatePost_ShouldThrowForUnauthorizedUser() {
        UpdatePostRequest request = new UpdatePostRequest();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> forumService.updatePost(1L, request, 2L, false))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void deletePost_ShouldSucceedForAuthor() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        forumService.deletePost(1L, 1L, false);

        verify(postRepository).deleteById(1L);
    }

    @Test
    void deleteComment_ShouldSucceedForAdmin() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        forumService.deleteComment(1L, 99L, true);

        verify(commentRepository).deleteById(1L);
    }
}
