package com.example.cms.controller;

import com.example.cms.service.CategoryService;
import com.example.cms.model.Category;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public List<Category> getAll() {
        log.info("Received request: GET /categories");
        return service.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getOne(@PathVariable Long id) {
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
    public Category create(@RequestBody Category category) {
        log.info("Received request: POST /categories");
        return service.createCategory(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category category) {
        log.info("Received request: UPDATE /categories/{}", id);
        return service.updateCategory(id, category)
            .map(current -> {
                log.info("Category with id {} found, returning 200 OK", id);
                return ResponseEntity.ok(current);
            }).orElseGet(() -> {
                log.info("Category with id {} not found, returning 404", id);
                return ResponseEntity.notFound().build();
            });
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> patch(@PathVariable Long id, @RequestBody Category category) {
        log.info("Received request: PATCH /categories/{}", id);
        return service.patchCategory(id, category)
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
        boolean deleted = service.deleteCategory(id);
        if (deleted) {
            log.info("Category with id {} found, returning 200 OK", id);
            return ResponseEntity.noContent().build();
        } else {
            log.info("Category with id {} not found, returning 404", id);
            return ResponseEntity.notFound().build();
        }
    }
}
