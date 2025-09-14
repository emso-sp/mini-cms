package com.example.cms.util;

import com.example.cms.model.Category;
import com.example.cms.dto.CategoryRequest;
import com.example.cms.dto.CategoryResponse;

import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    private Long nextId = 1L;
    
    // DTO -> Entity
    public Category toEntity(CategoryRequest request) {
        Category category = new Category();
        category.setId(nextId);
        nextId++;
        category.setName(request.name());
        category.setDescription(request.description());
        return category;
    }

    // Entity -> DTO
    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
