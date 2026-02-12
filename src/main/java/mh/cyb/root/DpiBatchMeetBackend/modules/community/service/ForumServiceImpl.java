package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.repository.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.mapper.ForumMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForumServiceImpl implements ForumService {

        private final ForumCategoryRepository categoryRepository;
        private final ForumPostRepository postRepository;
        private final ForumCommentRepository commentRepository;
        private final ForumMapper forumMapper;

        public ForumServiceImpl(ForumCategoryRepository categoryRepository,
                        ForumPostRepository postRepository,
                        ForumCommentRepository commentRepository,
                        ForumMapper forumMapper) {
                this.categoryRepository = categoryRepository;
                this.postRepository = postRepository;
                this.commentRepository = commentRepository;
                this.forumMapper = forumMapper;
        }

        @Override
        public List<ForumCategoryDto> getAllCategories() {
                return categoryRepository.findAll().stream()
                                .map(forumMapper::toDto)
                                .collect(Collectors.toList());
        }

        @Override
        public List<ForumPostDto> getPostsByCategory(Long categoryId) {
                return postRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId).stream()
                                .map(forumMapper::toDto)
                                .collect(Collectors.toList());
        }

        @Override
        public List<ForumPostDto> getAllPosts() {
                return postRepository.findAllByOrderByCreatedAtDesc().stream()
                                .map(forumMapper::toDto)
                                .collect(Collectors.toList());
        }

        @Override
        public ForumPostDto createPost(CreatePostRequest request, Long authorId) {
                ForumCategory category = categoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Category not found"));
                ForumPost post = ForumPost.builder()
                                .title(request.getTitle())
                                .content(request.getContent())
                                .authorId(authorId)
                                .category(category)
                                .build();
                return forumMapper.toDto(postRepository.save(post));
        }

        @Override
        public List<ForumCommentDto> getCommentsByPost(Long postId) {
                return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                                .map(forumMapper::toDto)
                                .collect(Collectors.toList());
        }

        @Override
        public ForumCommentDto addComment(Long postId, CreateCommentRequest request, Long authorId) {
                ForumPost post = postRepository.findById(postId)
                                .orElseThrow(() -> new RuntimeException("Post not found"));
                ForumComment comment = ForumComment.builder()
                                .content(request.getContent())
                                .authorId(authorId)
                                .post(post)
                                .build();
                return forumMapper.toDto(commentRepository.save(comment));
        }

        @Override
        public ForumCategoryDto createCategory(CreateCategoryRequest request) {
                ForumCategory category = forumMapper.toEntity(request);
                return forumMapper.toDto(categoryRepository.save(category));
        }

        @Override
        public ForumCategoryDto updateCategory(Long id, CreateCategoryRequest request) {
                ForumCategory category = categoryRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Category not found"));
                if (request.getName() != null)
                        category.setName(request.getName());
                if (request.getDescription() != null)
                        category.setDescription(request.getDescription());
                if (request.getIconUrl() != null)
                        category.setIconUrl(request.getIconUrl());
                return forumMapper.toDto(categoryRepository.save(category));
        }

        @Override
        public void deleteCategory(Long id) {
                categoryRepository.deleteById(id);
        }

        @Override
        public ForumPostDto getPostById(Long id) {
                ForumPost post = postRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Post not found"));
                return forumMapper.toDto(post);
        }

        @Override
        public ForumPostDto updatePost(Long id, UpdatePostRequest request, Long requesterId, boolean isAdmin) {
                ForumPost post = postRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Post not found"));
                if (!isAdmin && !post.getAuthorId().equals(requesterId)) {
                        throw new RuntimeException("Unauthorized to update this post");
                }
                if (request.getTitle() != null)
                        post.setTitle(request.getTitle());
                if (request.getContent() != null)
                        post.setContent(request.getContent());
                return forumMapper.toDto(postRepository.save(post));
        }

        @Override
        public void deletePost(Long id, Long requesterId, boolean isAdmin) {
                ForumPost post = postRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Post not found"));
                if (!isAdmin && !post.getAuthorId().equals(requesterId)) {
                        throw new RuntimeException("Unauthorized to delete this post");
                }
                postRepository.deleteById(id);
        }

        @Override
        public void deleteComment(Long id, Long requesterId, boolean isAdmin) {
                ForumComment comment = commentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Comment not found"));
                if (!isAdmin && !comment.getAuthorId().equals(requesterId)) {
                        throw new RuntimeException("Unauthorized to delete this comment");
                }
                commentRepository.deleteById(id);
        }
}
