package com.example.cms.controller;

import com.example.cms.service.BlogpostService;
import com.example.cms.dto.PostRequest;
import com.example.cms.dto.PostResponse;
import com.example.cms.dto.StatusRequest;
import com.example.cms.service.ServiceResult;

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
import org.springframework.web.bind.annotation.RequestParam;
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
    public List<PostResponse> getBlogposts(@RequestParam(required = false, name = "categoryId") List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return service.getAllBlogposts();
        } else {
            return service.getBlogpostsByCategory(categoryIds);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getOne(@PathVariable Long id) {
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
    public ResponseEntity<PostResponse> create(@RequestBody PostRequest request) {
        log.info("Received request: POST /blogposts");
        final ServiceResult<PostResponse> result = service.createBlogpost(request);
        return switch (result.getStatus()) {
            case OK -> {
                log.info("Blogpost creation successful, return 200 OK");
                yield ResponseEntity.ok(result.getData());
            }
            case INVALID_INPUT -> {
                log.warn("Invalid input, return 400 Bad Request");
                yield ResponseEntity.badRequest().build();
            }
            case NOT_FOUND -> {
                log.warn("Blogpost not found, return 404 Not Found");
                yield ResponseEntity.notFound().build();
            }
        };
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable Long id, @RequestBody PostRequest request) {
        log.info("Received request: PUT /blogposts/{}", id);
        final ServiceResult<PostResponse> result = service.updateBlogpost(id, request);
        return switch (result.getStatus()) {
            case OK -> {
                log.info("Blogpost creation successful, return 200 OK");
                yield ResponseEntity.ok(result.getData());
            }
            case INVALID_INPUT -> {
                log.warn("Invalid input, return 400 Bad Request");
                yield ResponseEntity.badRequest().build();
            }
            case NOT_FOUND -> {
                log.warn("Blogpost not found, return 404 Not Found");
                yield ResponseEntity.notFound().build();
            }
        };
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PostResponse> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        log.info("Received request: PUT /blogposts/{}/status", id);

        final ServiceResult<PostResponse> result = service.updateStatus(id, request);
        return switch (result.getStatus()) {
            case OK -> {
                log.info("Status of blogpost {} successfully updated", id);
                yield ResponseEntity.ok(result.getData());
            }
            case INVALID_INPUT -> {
                log.warn("Invalid input, Status can only be DRAFT, PUBLISHED, and ARCHIVED, return 400 Bad Request");
                yield ResponseEntity.badRequest().build();
            }
            case NOT_FOUND -> {
                log.warn("Blogpost not found, return 404 Not Found");
                yield ResponseEntity.notFound().build();
            }
        };
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostResponse> patch(@PathVariable Long id, @RequestBody PostRequest request) {
        log.info("Received request: PATCH /blogposts/{}", id);
        return service.patchBlogpost(id, request)
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
        final boolean deleted = service.deleteBlogpost(id);
        if (deleted) {
            log.info("Blogpost with id {} found, returning 204 OK", id);
            return ResponseEntity.noContent().build();
        } else {
            log.info("Blogpost with id {} not found, returning 404", id);
            return ResponseEntity.notFound().build();
        }
        
    }
}
