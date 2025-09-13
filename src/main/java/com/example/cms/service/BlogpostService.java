package com.example.cms.service;

import com.example.cms.repository.BlogpostRepository;
import com.example.cms.repository.CategoryRepository;
import com.example.cms.model.Blogpost;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BlogpostService {
    private static final Logger log = LoggerFactory.getLogger(BlogpostService.class);
    private final BlogpostRepository repository;
    private final CategoryRepository categoryRepository;

    public BlogpostService(BlogpostRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }
    
    public List<Blogpost> getAllBlogposts() {
        List<Blogpost> blogposts = repository.findAll();
        log.info("Successfully fetched {} blogposts", blogposts.size());
        return blogposts;
    }

    public Optional<Blogpost> getBlogpost(Long id) {
        return repository.findById(id);
    }

    // Helper method: avoid adding categories to blogposts that don't exist
    private boolean validCategories(List<Long> categories) {
        if (categories == null) {
            return true;
        }
        return categories.stream().allMatch(id -> categoryRepository.findById(id).isPresent());
    }

    public Optional<Blogpost> createBlogpost(Blogpost blogpost) {
        if (!validCategories(blogpost.getCategories())) {
            log.warn("Invalid categories: Blogpost creation unsuccessful");
            return Optional.empty();
        }
        blogpost.setCreatedAt(LocalDateTime.now());
        Blogpost newBlogpost = repository.save(blogpost);
        log.info("Successfully created blogpost with id {}", newBlogpost.getId());
        return Optional.of(newBlogpost);
    }

    public Optional<Blogpost> updateBlogpost(Long id, Blogpost updatedBlogpost) {
        Optional<Blogpost> blogpost = repository.findById(id);
        if (blogpost.isPresent()) {
            if(!validCategories(updatedBlogpost.getCategories())) {
                log.warn("Invalid categories: Blogpost update unsuccessful");
                return Optional.empty();
            }
            Blogpost current = blogpost.get();
            current.setTitle(updatedBlogpost.getTitle());
            current.setAuthor(updatedBlogpost.getAuthor());
            current.setContent(updatedBlogpost.getContent());
            current.setCategories(updatedBlogpost.getCategories());
            repository.save(current);
            log.info("Successfully updated blogpost with id {}", id);
            return Optional.of(current);
        }
        log.warn("Blogpost with id {} not found", id);
        return Optional.empty();
    }

    public Optional<Blogpost> patchBlogpost(Long id, Blogpost partialUpdate) {
        Optional<Blogpost> blogpost = repository.findById(id);
        if (blogpost.isPresent()) {
            Blogpost current = blogpost.get();
            if (partialUpdate.getCategories() != null) {
                if (!validCategories(partialUpdate.getCategories())) {
                    log.warn("Invalid categories: Partial blogpost update unsuccessful");
                    return Optional.empty();
                }
                current.setCategories(partialUpdate.getCategories());
            }
            if (partialUpdate.getTitle() != null) {
                current.setTitle(partialUpdate.getTitle());
            }
            if (partialUpdate.getContent() != null) {
                current.setContent(partialUpdate.getContent());
            }
            if (partialUpdate.getAuthor() != null) {
                current.setAuthor(partialUpdate.getAuthor());
            }
            repository.save(current);
            log.info("Successfully updated blogpost with id {} partially", id);
            return Optional.of(current);
        }
        log.warn("Blogpost with id {} not found", id);
        return Optional.empty();
    }

    public boolean deleteBlogpost(Long id) {
        if (!repository.findById(id).isPresent()) {
            log.warn("Blogpost with id {} not found. Deleting blogpost unsuccessful", id);
            return false; 
        }
        repository.deleteById(id);
        log.info("Successfully deleted blogpost with id {}", id);
        return true;
    }

}
