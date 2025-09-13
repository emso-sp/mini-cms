package com.example.cms.service;

import com.example.cms.repository.BlogpostRepository;
import com.example.cms.model.Blogpost;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.LocalDateTime;

@Service
public class BlogpostService {
    private final BlogpostRepository repository;

    public BlogpostService(BlogpostRepository repository) {
        this.repository = repository;
    }
    
    public List<Blogpost> getAllBlogposts() {
        return repository.findAll();
    }

    public Optional<Blogpost> getBlogpost(Long id) {
        return repository.findById(id);
    }

    public Blogpost createBlogpost(Blogpost blogpost) {
        blogpost.setCreatedAt(LocalDateTime.now());
        return repository.save(blogpost);
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
            return Optional.of(current);
        }
        return Optional.empty();
    }

    public boolean deleteBlogpost(Long id) {
        if (!repository.findById(id).isPresent()) {
            return false; 
        }
        repository.deleteById(id);
        return true;
    }

}
