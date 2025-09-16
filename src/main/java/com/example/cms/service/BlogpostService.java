package com.example.cms.service;

import com.example.cms.repository.BlogpostRepository;
import com.example.cms.repository.BlogpostVersionRepository;
import com.example.cms.repository.CategoryRepository;
import com.example.cms.model.Blogpost;
import com.example.cms.model.BlogpostVersion;
import com.example.cms.model.Status;
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
    private final BlogpostVersionRepository versionRepository;
    private final CategoryRepository categoryRepository;
    private final PostMapper postMapper;
    private Long nextId = 1L; 

    public BlogpostService(BlogpostRepository repository, BlogpostVersionRepository versionRepository, CategoryRepository categoryRepository, PostMapper postMapper) {
        this.repository = repository;
        this.versionRepository = versionRepository;
        this.categoryRepository = categoryRepository;
        this.postMapper = postMapper;
    }
    
    public List<PostResponse> getAllBlogposts() {
        List<Blogpost> blogposts = repository.findAll();
        log.info("Successfully fetched {} blogposts", blogposts.size());

        List<BlogpostVersion> currentVersions = new ArrayList<>();
        for (Blogpost post : blogposts) {
            BlogpostVersion current = versionRepository.findById(post.getCurrentVersion()).get();
            currentVersions.add(current);
        }
        log.info("Successfully fetched {} current versions for each blogpost", currentVersions.size());
        return currentVersions.stream().map(postMapper::toResponse).toList();
    }

    public Optional<PostResponse> getBlogpost(final Long id) {
        Optional<Blogpost> blogpost = repository.findById(id);
        if (blogpost.isEmpty()) {
            return Optional.empty();
        }
        Long currentVersionId = blogpost.get().getCurrentVersion();
        if (currentVersionId == null) { return Optional.empty(); }
        return versionRepository.findById(currentVersionId)
            .map(postMapper::toResponse);
    }

    public List<PostResponse> getBlogpostsByCategory(final List<Long> categoryIds) {
        log.info("Filter blogposts by categories {}", categoryIds);
        List<Blogpost> blogposts = repository.findAll();
        List<PostResponse> currentVersionsWithCategory = new ArrayList<>();
        for (Blogpost post : blogposts) {
            BlogpostVersion current = versionRepository.findById(post.getCurrentVersion()).get();
            if (current.getCategories().containsAll(categoryIds)) {
                currentVersionsWithCategory.add(postMapper.toResponse(current));
            }
        }
        return currentVersionsWithCategory;
    }

    public Optional<List<PostResponse>> getAllVersionsOfBlogpost(Long id) {
        Optional<Blogpost> blogpost = repository.findById(id);
        if (blogpost.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(versionRepository.findByBlogpostId(id).stream().map(postMapper::toResponse).toList());
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
        if (!validCategories(request.categoryIds())) {
            log.warn("Invalid categories: Blogpost creation unsuccessful");
            return ServiceResult.invalidInput();
        }

        Blogpost blogpost = new Blogpost();
        blogpost.setId(nextId);
        nextId++;

        BlogpostVersion version = postMapper.toEntity(request);
        version.setBlogpostId(blogpost.getId());
        version.setVersionNumber(1);
        BlogpostVersion savedVersion = versionRepository.save(version);

        blogpost.setCurrentVersion(savedVersion.getId());
        blogpost.getVersionIds().add(savedVersion.getId());
        repository.save(blogpost);

        log.info("Successfully created blogpost with id {} version {}", blogpost.getId(), savedVersion.getVersionNumber());
        return ServiceResult.ok(postMapper.toResponse(savedVersion));
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

        Blogpost post = existing.get();
        log.info("ID of current version of blogpost: {}", post.getCurrentVersion());
        BlogpostVersion currentVersion = versionRepository.findById(post.getCurrentVersion()).get();
        BlogpostVersion newVersion = postMapper.toEntity(request);
        newVersion.setBlogpostId(currentVersion.getBlogpostId());
        newVersion.setVersionNumber(versionRepository.findByBlogpostId(id).stream()
            .map(BlogpostVersion::getVersionNumber)
            .max(Integer::compareTo)
            .get() + 1);
        BlogpostVersion savedVersion = versionRepository.save(newVersion);
        post.setCurrentVersion(savedVersion.getId());
        post.getVersionIds().add(savedVersion.getId());
        repository.save(post);
        log.info("Successfully updated blogpost with id {} to new version {}", post.getId(), savedVersion.getVersionNumber());

        return ServiceResult.ok(postMapper.toResponse(newVersion));
    }

    public ServiceResult<PostResponse> updateStatus(final Long id, final StatusRequest request) {
        Optional<Blogpost> existing = repository.findById(id);
        if (existing.isEmpty()) {
            log.warn("Blogpost with id {} not found", id);
            return ServiceResult.notFound();
        }
        Blogpost post = existing.get();
        BlogpostVersion currentVersion = versionRepository.findById(post.getCurrentVersion()).get();
        currentVersion.setStatus(request.status());
        versionRepository.save(currentVersion);
        log.info("Version {} of blogpost with id {} is set to {}", currentVersion.getVersionNumber(), id, currentVersion.getStatus());

        // if the current version is now published, set previously published version to archived
        if (currentVersion.getStatus() == Status.PUBLISHED) {
            for (BlogpostVersion version : versionRepository.findByBlogpostId(id)) {
                if (!version.getId().equals(currentVersion.getId()) && version.getStatus() == Status.PUBLISHED) {
                    version.setStatus(Status.ARCHIVED);
                    versionRepository.save(version);
                    log.info("Previously published version {} of blogpost with id {} set to archived", version.getVersionNumber(), id);
                }
            }
        }
        return ServiceResult.ok(postMapper.toResponse(currentVersion));
    }

    public ServiceResult<PostResponse> rollbackBlogpost(final Long id, final Integer versionNumber) {
        Optional<Blogpost> existing = repository.findById(id);
        if (existing.isEmpty()) {
            log.warn("Blogpost with id {} not found", id);
            return ServiceResult.notFound();
        }
        Blogpost post = existing.get();
        Optional<BlogpostVersion> targetVersionOpt = versionRepository.findByBlogpostId(id).stream().filter(v -> v.getVersionNumber().equals(versionNumber)).findFirst();
        if (targetVersionOpt.isEmpty()) {
            log.warn("Version number {} not found in existing version numbers {}", versionNumber, post.getVersionIds());
            return ServiceResult.invalidInput();
        }
        BlogpostVersion targetVersion = targetVersionOpt.get();
        BlogpostVersion prevCurrentVersion = versionRepository.findById(post.getCurrentVersion()).get();
        post.setCurrentVersion(targetVersion.getId());
        // new current version is set to DRAFT, previous current version to ARCHIVED
        targetVersion.setStatus(Status.DRAFT);
        prevCurrentVersion.setStatus(Status.ARCHIVED);
        // look at categories of new version to make sure every category still exists (because when deleting a category fully, not every version of a post is checked)
        for (Long categoryId : targetVersion.getCategories()) {
            if (categoryRepository.findById(categoryId).isEmpty()) {
                targetVersion.getCategories().remove(categoryId);
            }
        }
        repository.save(post);
        versionRepository.save(prevCurrentVersion);
        versionRepository.save(targetVersion);

        log.info("Blogpost with id {} is set to version {}", id, targetVersion.getVersionNumber());
        log.info("Previous latest version ({}) of blogpost is set to ARCHIVED", prevCurrentVersion.getVersionNumber());

        return ServiceResult.ok(postMapper.toResponse(targetVersion));
    }

    public ServiceResult<PostResponse> patchBlogpost(final Long id, final PostRequest request) {
        Optional<Blogpost> blogpost = repository.findById(id);
        if (!blogpost.isPresent()) {
            log.warn("Blogpost with id {} not found", id);
            return ServiceResult.notFound();
        }
        if (request.categoryIds() != null && !validCategories(request.categoryIds())) {
            log.warn("Invalid categories: partial blogpost update unsuccessful");
            return ServiceResult.invalidInput();
        } 

        Blogpost post = blogpost.get();
        BlogpostVersion current = versionRepository.findById(blogpost.get().getCurrentVersion()).get();
        BlogpostVersion newVersion = postMapper.toEntity(request);
        log.info("Current content of newVersion: author - {}, content - {}, title - {}, categories - {}", newVersion.getAuthor(), newVersion.getContent(), newVersion.getTitle(), newVersion.getCategories());
        if (request.title() == null) {
            newVersion.setTitle(current.getTitle());
        }
        if (request.author() == null) {
            newVersion.setAuthor(current.getAuthor());
        }
        if (request.content() == null) {
            newVersion.setContent(current.getContent());
        }
        if (request.categoryIds() == null) {
            newVersion.setCategories(current.getCategories());
        }
        newVersion.setVersionNumber(versionRepository.findByBlogpostId(id).stream()
            .map(BlogpostVersion::getVersionNumber)
            .max(Integer::compareTo)
            .get() + 1);
        newVersion.setBlogpostId(current.getBlogpostId());
        versionRepository.save(newVersion);
        post.setCurrentVersion(newVersion.getId());
        post.getVersionIds().add(newVersion.getId());
        repository.save(post);
        
        log.info("Successfully updated blogpost with id {} to new version {}", id, newVersion.getVersionNumber());
        return ServiceResult.ok(postMapper.toResponse(newVersion));
    }

    public boolean deleteBlogpost(final Long id) {
        if (!repository.findById(id).isPresent()) {
            log.warn("Blogpost with id {} not found. Deleting blogpost unsuccessful", id);
            return false; 
        }
        versionRepository.deleteByBlogpostId(id);
        repository.deleteById(id);
        log.info("Successfully deleted blogpost with id {}", id);
        return true;
    }

}
