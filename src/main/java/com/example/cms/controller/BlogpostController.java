package com.example.cms.controller;

import com.example.cms.service.BlogpostService;
import com.example.cms.model.Blogpost;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/blogposts")
public class BlogpostController {
    private final BlogpostService service; 

    public BlogpostController(BlogpostService service) {
        this.service = service;
    }

    @GetMapping
    public List<Blogpost> getAll() {
        return service.getAllBlogposts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blogpost> getOne(@PathVariable Long id) {
        return service.getBlogpost(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Blogpost create(@RequestBody Blogpost blogpost) {
        Blogpost newBlogpost = service.createBlogpost(blogpost);
        return newBlogpost;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Blogpost> update(@PathVariable Long id, @RequestBody Blogpost blogpost) {
        return service.updateBlogpost(id, blogpost).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = service.deleteBlogpost(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
        
    }
}
