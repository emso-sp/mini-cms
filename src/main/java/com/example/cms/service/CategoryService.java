package com.example.cms.service;

import com.example.cms.repository.CategoryRepository;
import com.example.cms.repository.BlogpostRepository;
import com.example.cms.model.Category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import org.springframework.stereotype.Service;


@Service
public class CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository repository;
    private final BlogpostRepository blogpostRepository;

    public CategoryService(CategoryRepository repository, BlogpostRepository blogpostRepository) {
        this.repository = repository;
        this.blogpostRepository = blogpostRepository;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = repository.findAll();
        log.info("Successfully fetched {} categories", categories.size());
        return categories;
    }

    public Optional<Category> getCategory(Long id) {
        return repository.findById(id);
    }

    public Category createCategory(Category category) {
        repository.save(category);
        log.info("Successfully created new category: {}", category.getId());
        return category;
    }

    public Optional<Category> updateCategory(Long id, Category updatedCategory) {
        Optional<Category> category = repository.findById(id);
        if (category.isPresent()) {
            Category current = category.get();
            current.setName(updatedCategory.getName());
            current.setDescription(updatedCategory.getDescription());
            repository.save(current);
            log.info("Successfully updated category {}", id);
            return Optional.of(current);
        }
        return Optional.empty();
    }

    public Optional<Category> patchCategory(Long id, Category partialUpdate) {
        Optional<Category> category = repository.findById(id);
        if (category.isPresent()) {
            Category current = category.get();
            if (partialUpdate.getName() != null) {
                current.setName(partialUpdate.getName());
            }
            if (partialUpdate.getDescription() != null) {
                current.setDescription(partialUpdate.getDescription());
            }
            repository.save(current);
            log.info("Successfully updated category {} partially", id);
            return Optional.of(current);
        }
        return Optional.empty();
    }

    public boolean deleteCategory(Long id) {
        if (repository.findById(id).isEmpty()) { return false; }
        repository.deleteById(id);
        // loop through blogpostRepository to remove references to deleted category
        blogpostRepository.findAll().forEach(blogpost -> {
            blogpost.getCategories().remove(id);
        });
        return true;
    }

}
