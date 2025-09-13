package com.example.cms.service;

import com.example.cms.repository.BlogpostRepository;
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

    public BlogpostService(BlogpostRepository repository) {
        this.repository = repository;
    }
    
    public List<Blogpost> getAllBlogposts() {
        List<Blogpost> blogposts = repository.findAll();
        log.info("Successfully fetched {} blogposts", blogposts.size());
        return blogposts;
    }

    public Optional<Blogpost> getBlogpost(Long id) {
        return repository.findById(id);
    }

    public Blogpost createBlogpost(Blogpost blogpost) {
        blogpost.setCreatedAt(LocalDateTime.now());
        Blogpost newBlogpost = repository.save(blogpost);
        log.info("Successfully created blogpost with id {}", newBlogpost.getId());
        return newBlogpost;
    }

    public Optional<Blogpost> updateBlogpost(Long id, Blogpost updatedBlogpost) {
        Optional<Blogpost> blogpost = repository.findById(id);
        if (blogpost.isPresent()) {
            Blogpost current = blogpost.get();
            current.setTitle(updatedBlogpost.getTitle());
            current.setAuthor(updatedBlogpost.getAuthor());
            current.setContent(updatedBlogpost.getContent());
            current.setCategories(updatedBlogpost.getCategories());
            repository.save(current);
            log.info("Successfully updated blogpost with id {}", id);
            return Optional.of(current);
        }
        return Optional.empty();
    }

    public Optional<Blogpost> patchBlogpost(Long id, Blogpost partialUpdate) {
        Optional<Blogpost> blogpost = repository.findById(id);
        if (blogpost.isPresent()) {
            Blogpost current = blogpost.get();
            if (partialUpdate.getTitle() != null) {
                current.setTitle(partialUpdate.getTitle());
            }
            if (partialUpdate.getContent() != null) {
                current.setContent(partialUpdate.getContent());
            }
            if (partialUpdate.getAuthor() != null) {
                current.setAuthor(partialUpdate.getAuthor());
            }
            if (partialUpdate.getCategories() != null) {
                current.setCategories(partialUpdate.getCategories());
            }
            repository.save(current);
            log.info("Successfully updated blogpost with id {} partially", id);
            return Optional.of(current);
        }
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
