package com.example.cms.service;

import com.example.cms.repository.CategoryRepository;
import com.example.cms.repository.BlogpostRepository;
import com.example.cms.model.Category;
import com.example.cms.dto.CategoryRequest;
import com.example.cms.dto.CategoryResponse;
import com.example.cms.util.CategoryMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import org.springframework.stereotype.Service;


@Service
public class CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository repository;
    private final BlogpostRepository blogpostRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository repository, BlogpostRepository blogpostRepository, CategoryMapper categoryMapper) {
        this.repository = repository;
        this.blogpostRepository = blogpostRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = repository.findAll();
        log.info("Successfully fetched {} categories", categories.size());
        return categories.stream()
            .map(categoryMapper::toResponse)
            .toList();
    }

    public Optional<CategoryResponse> getCategory(Long id) {
        return repository.findById(id).map(categoryMapper::toResponse);
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        Category newCategory = categoryMapper.toEntity(request);
        repository.save(newCategory);
        log.info("Successfully created new category: {}", newCategory.getId());
        return categoryMapper.toResponse(newCategory);
    }

    public Optional<CategoryResponse> updateCategory(Long id, CategoryRequest request) {
        Optional<Category> category = repository.findById(id);
        if (category.isPresent()) {
            Category current = category.get();
            current.setName(request.name());
            current.setDescription(request.description());
            repository.save(current);
            log.info("Successfully updated category {}", id);
            return Optional.of(categoryMapper.toResponse(current));
        }
        return Optional.empty();
    }

    public Optional<CategoryResponse> patchCategory(Long id, CategoryRequest request) {
        Optional<Category> category = repository.findById(id);
        if (category.isPresent()) {
            Category current = category.get();
            if (request.name() != null) {
                current.setName(request.name());
            }
            if (request.description() != null) {
                current.setDescription(request.description());
            }
            repository.save(current);
            log.info("Successfully updated category {} partially", id);
            return Optional.of(categoryMapper.toResponse(current));
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
