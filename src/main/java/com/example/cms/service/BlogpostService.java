package com.example.cms.service;

import com.example.cms.repository.BlogpostRepository;
import com.example.cms.repository.CategoryRepository;
import com.example.cms.model.Blogpost;
import com.example.cms.dto.PostRequest;
import com.example.cms.dto.PostResponse;
import com.example.cms.dto.StatusRequest;
import com.example.cms.util.PostMapper;
import com.example.cms.service.ServiceResult;
import org.springframework.stereotype.Service;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BlogpostService {
    private static final Logger log = LoggerFactory.getLogger(BlogpostService.class);
    private final BlogpostRepository repository;
    private final CategoryRepository categoryRepository;
    private final PostMapper postMapper;

    public BlogpostService(BlogpostRepository repository, CategoryRepository categoryRepository, PostMapper postMapper) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.postMapper = postMapper;
    }
    
    public List<PostResponse> getAllBlogposts() {
        List<Blogpost> blogposts = repository.findAll();
        log.info("Successfully fetched {} blogposts", blogposts.size());
        return blogposts.stream()
                .map(postMapper::toResponse)
                .toList();
    }

    public Optional<PostResponse> getBlogpost(final Long id) {
        return repository.findById(id)
                .map(postMapper::toResponse);
    }

    public List<PostResponse> getBlogpostsByCategory(final List<Long> categoryIds) {
        log.info("Filter blogposts by categories {}", categoryIds);
        return repository.findAll().stream()
                .filter(blogpost -> blogpost.getCategories().containsAll(categoryIds))
                .map(postMapper::toResponse)
                .toList();

    }

    // Helper method: avoid adding categories to blogposts that don't exist
    private boolean validCategories(final List<Long> categories) {
        log.info("Validating categories {}", categories);
        if (categories == null) {
            return true;
        }
        return categories.stream().allMatch(id -> categoryRepository.findById(id).isPresent());
    }

    // Helper method: invalid title
    private boolean invalidTitle(final PostRequest request) {
        return (request.title() == null || request.title().isEmpty());
    }

    // Helper method: invalid content
    private boolean invalidContent(final PostRequest request) {
        return (request.content() == null || request.content().isEmpty());
    }

    // Helper method: invalid author
    private boolean invalidAuthor(final PostRequest request) {
        return (request.author() == null || request.author().isEmpty());
    }

    public ServiceResult<PostResponse> createBlogpost(final PostRequest request) {
        if (invalidTitle(request) || invalidContent(request) || invalidAuthor(request)) {
            log.warn("Title, content and author cannot be null or empty");
            return ServiceResult.invalidInput();
        }
        boolean valid = validCategories(request.categoryIds());
        log.info("Are categories valid? {}", valid);
        if (!validCategories(request.categoryIds())) {
            log.warn("Invalid categories: Blogpost creation unsuccessful");
            return ServiceResult.invalidInput();
        }
        Blogpost blogpost = postMapper.toEntity(request);
        final Blogpost saved = repository.save(blogpost);
        log.info("Successfully created blogpost with id {}", saved.getId());
        return ServiceResult.ok(postMapper.toResponse(saved));
    }

    public ServiceResult<PostResponse> updateBlogpost(final Long id, final PostRequest request) {
        Optional<Blogpost> existing = repository.findById(id);
        if (existing.isEmpty()) {
            log.warn("Blogpost with id {} not found", id);
            return ServiceResult.notFound();
        }
        if (invalidTitle(request) || invalidContent(request) || invalidAuthor(request)) {
            log.warn("Title, content and author cannot be null or empty");
            return ServiceResult.invalidInput();
        }
        if (!validCategories(request.categoryIds())) {
            log.warn("Invalid categories: Blogpost update unsuccessful");
            return ServiceResult.invalidInput();
        }

        Blogpost current = existing.get();
        current.setTitle(request.title());
        current.setAuthor(request.author());
        current.setContent(request.content());
        current.setCategories(
            Optional.ofNullable(request.categoryIds())
                    .orElse(new ArrayList<>())
        );
        repository.save(current);
        log.info("Successfully updated blogpost with id {}", id);
        return ServiceResult.ok(postMapper.toResponse(current));
    }

    public ServiceResult<PostResponse> updateStatus(final Long id, final StatusRequest request) {
        Optional<Blogpost> existing = repository.findById(id);
        if (existing.isEmpty()) {
            log.warn("Blogpost with id {} not found", id);
            return ServiceResult.notFound();
        }

        Blogpost current = existing.get();
        current.setStatus(request.status());
        repository.save(current);
        log.info("Successfully updated blogpost with id {}", id);
        return ServiceResult.ok(postMapper.toResponse(current));
    }

    public Optional<PostResponse> patchBlogpost(final Long id, final PostRequest request) {
        Optional<Blogpost> blogpost = repository.findById(id);
        if (blogpost.isPresent()) {
            Blogpost current = blogpost.get();
            if (request.categoryIds() != null) {
                if (!validCategories(request.categoryIds())) {
                    log.warn("Invalid categories: Partial blogpost update unsuccessful");
                    return Optional.empty();
                }
                if (current.getCategories() == null) {
                    current.setCategories(new ArrayList<>());
                }
                current.setCategories(request.categoryIds());
                //current.getCategories().addAll(request.categoryIds());
            }
            if (request.title() != null) {
                current.setTitle(request.title());
            }
            if (request.content() != null) {
                current.setContent(request.content());
            }
            if (request.author() != null) {
                current.setAuthor(request.author());
            }
            repository.save(current);
            log.info("Successfully updated blogpost with id {} partially", id);
            return Optional.of(postMapper.toResponse(current));
        }
        log.warn("Blogpost with id {} not found", id);
        return Optional.empty();
    }

    public boolean deleteBlogpost(final Long id) {
        if (!repository.findById(id).isPresent()) {
            log.warn("Blogpost with id {} not found. Deleting blogpost unsuccessful", id);
            return false; 
        }
        repository.deleteById(id);
        log.info("Successfully deleted blogpost with id {}", id);
        return true;
    }

}
