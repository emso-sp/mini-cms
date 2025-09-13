package com.example.cms.controller;

import com.example.cms.service.BlogpostService;
import com.example.cms.model.Blogpost;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/blogposts")
public class BlogpostController {
    private static final Logger log = LoggerFactory.getLogger(BlogpostController.class);
    private final BlogpostService service; 

    public BlogpostController(BlogpostService service) {
        this.service = service;
    }

    @GetMapping
    public List<Blogpost> getAll() {
        log.info("Received request: GET /blogposts");
        return service.getAllBlogposts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blogpost> getOne(@PathVariable Long id) {
        log.info("Received request: GET /blogposts/{}", id);
        return service.getBlogpost(id)
            .map(blogpost -> {
                log.info("Blogpost with id {} found, returning 200 OK", id);
                return ResponseEntity.ok(blogpost);
            }).orElseGet(() -> {
                log.warn("Blogpost with id {} not found, returning 404", id);
                return ResponseEntity.notFound().build();
            });
    }

    @PostMapping
    public ResponseEntity<Blogpost> create(@RequestBody Blogpost blogpost) {
        log.info("Received request: POST /blogposts");
        return service.createBlogpost(blogpost)
            .map(newBlogpost -> {
                log.info("Category creation successful, return 200 OK");
                return ResponseEntity.ok(newBlogpost);
            }).orElseGet(() -> {
                log.warn("Return badRequest");
                return ResponseEntity.badRequest().build();
            });
    }

    @PutMapping("/{id}")
    public ResponseEntity<Blogpost> update(@PathVariable Long id, @RequestBody Blogpost blogpost) {
        log.info("Received request: PUT /blogposts/{}", id);
        return service.updateBlogpost(id, blogpost)
            .map(current -> {
                log.info("Blogpost with id {} found, returning 200 OK", id);
                return ResponseEntity.ok(current);
            }).orElseGet(() -> {
                log.warn("Returning 404", id);
                return ResponseEntity.notFound().build();
            });
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Blogpost> patch(@PathVariable Long id, @RequestBody Blogpost blogpost) {
        log.info("Received request: PATCH /blogposts/{}", id);
        return service.patchBlogpost(id, blogpost)
            .map(current -> {
                log.info("Blogpost with id {} found, returning 200 OK", id);
                return ResponseEntity.ok(current);
            }).orElseGet(() -> {
                log.warn("Returning 404", id);
                return ResponseEntity.notFound().build();
            });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Received request: DELETE /blogposts/{}", id);
        boolean deleted = service.deleteBlogpost(id);
        if (deleted) {
            log.info("Blogpost with id {} found, returning 200 OK", id);
            return ResponseEntity.noContent().build();
        } else {
            log.info("Blogpost with id {} not found, returning 404", id);
            return ResponseEntity.notFound().build();
        }
        
    }
}
