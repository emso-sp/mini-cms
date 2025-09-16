package com.example.cms.util;

import com.example.cms.model.BlogpostVersion;
import com.example.cms.model.Category;
import com.example.cms.model.Status;
import com.example.cms.repository.CategoryRepository;

import com.example.cms.dto.PostRequest;
import com.example.cms.dto.PostResponse;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    private final CategoryRepository categoryRepository;

    public PostMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // DTO -> Entity
    public BlogpostVersion toEntity(final PostRequest request) {
        BlogpostVersion version = new BlogpostVersion();
        version.setTitle(request.title());
        version.setAuthor(request.author());
        version.setContent(request.content());
        version.setCategories(request.categoryIds());
        version.setStatus(Status.DRAFT);
        version.setCreatedAt(LocalDateTime.now());
        return version;
    }

    // Entity -> DTO
    public PostResponse toResponse(final BlogpostVersion version) {
        List<String> categoryNames = version.getCategories().stream()
            .map(id -> categoryRepository.findById(id)   
                    .map(Category::getName)             
                    .orElse("[Category not found]")) 
            .toList();

        return new PostResponse(
            version.getBlogpostId(),
            version.getVersionNumber(),
            version.getTitle(),
            version.getAuthor(),
            version.getContent(),
            version.getCreatedAt(),
            version.getStatus(),
            categoryNames
        );
    }
}
