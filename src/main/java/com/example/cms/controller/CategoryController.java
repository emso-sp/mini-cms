package com.example.cms.controller;

import com.example.cms.service.CategoryService;
import com.example.cms.service.ServiceResult;
import com.example.cms.dto.CategoryRequest;
import com.example.cms.dto.CategoryResponse;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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


@RestController
@RequestMapping("/categories")
public class CategoryController {
    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoryResponse> getAll() {
        log.info("Received request: GET /categories");
        return service.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getOne(@PathVariable Long id) {
        log.info("Received request: GET /categories/{}", id);
        return service.getCategory(id)
            .map(category -> {
                log.info("Category with id {} found, returning 200 OK", id);
                return ResponseEntity.ok(category);
            }).orElseGet(() -> {
                log.info("Category with id {} not found, returning 404", id);
                return ResponseEntity.notFound().build();
            });
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request) {
        log.info("Received request: POST /categories");
           
        final ServiceResult<CategoryResponse> result = service.createCategory(request);
        return switch (result.getStatus()) {
            case OK -> {
                log.info("Category successfully created, returning 200 OK");
                yield ResponseEntity.ok(result.getData());
            }
            case INVALID_INPUT -> {
                log.warn("Name cannot be null or empty, returning 400 Bad Request");
                yield ResponseEntity.badRequest().build();
            }
            case NOT_FOUND -> {
                log.warn("Category not found, returning 404 Not Found");
                yield ResponseEntity.notFound().build();
            }
        };
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        log.info("Received request: UPDATE /categories/{}", id);

        final ServiceResult<CategoryResponse> result = service.updateCategory(id, request);
        return switch(result.getStatus()) {
            case OK -> {
                log.info("Category with id {} found, returning 200 OK", id);
                yield ResponseEntity.ok(result.getData());
            }
            case INVALID_INPUT -> {
                log.warn("Name cannot be null or empty, returning 400 Bad Request");
                yield ResponseEntity.badRequest().build();
            }
            case NOT_FOUND -> {
                log.warn("Category not found, returning 404 Not Found");
                yield ResponseEntity.notFound().build();
            }
        };
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> patch(@PathVariable Long id, @RequestBody CategoryRequest request) {
        log.info("Received request: PATCH /categories/{}", id);
        return service.patchCategory(id, request)
            .map(current -> {
                log.info("Category with id {} found, returning 200 OK", id);
                return ResponseEntity.ok(current);
            }).orElseGet(() -> {
                log.info("Category with id {} not found, returning 404", id);
                return ResponseEntity.notFound().build();
            });
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Received request: DELETE /categories/{}", id);
        final boolean deleted = service.deleteCategory(id);
        if (deleted) {
            log.info("Category with id {} found, returning 204 OK", id);
            return ResponseEntity.noContent().build();
        } else {
            log.info("Category with id {} not found, returning 404", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/safe")
    public ResponseEntity<Void> safeDelete(@PathVariable Long id) {
        // deletes category only when it is not used in any blogpost
        log.info("Received request: DELETE /categories/{}/safe", id);
        final boolean deleted = service.deleteCategorySafely(id);

        if (deleted) {
            log.info("Returning 204 ok");
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
        
    }
}
