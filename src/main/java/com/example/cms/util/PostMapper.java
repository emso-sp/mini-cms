package com.example.cms.util;

import com.example.cms.model.Blogpost;
import com.example.cms.model.Category;
import com.example.cms.repository.CategoryRepository;
import com.example.cms.dto.PostRequest;
import com.example.cms.dto.PostResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    
    private Long nextId = 1L;
    private final CategoryRepository categoryRepository;

    public PostMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // DTO -> Entity
    public Blogpost toEntity(PostRequest request) {
        Blogpost blogpost = new Blogpost();
        blogpost.setId(nextId);
        nextId++;
        blogpost.setTitle(request.title());
        blogpost.setAuthor(request.author());
        blogpost.setContent(request.content());
        blogpost.setCreatedAt(LocalDateTime.now());
        blogpost.setCategories(
            Optional.ofNullable(request.categoryIds())
                .orElse(new ArrayList<>())
        );
        return blogpost;
    }

    // Entity -> DTO
    public PostResponse toResponse(Blogpost blogpost) {
        List<String> categoryNames = blogpost.getCategories().stream()
            .map(id -> categoryRepository.findById(id)   
                    .map(Category::getName)             
                    .orElse("[Category not found]")) 
            .toList();

        return new PostResponse(
            blogpost.getId(),
            blogpost.getTitle(),
            blogpost.getAuthor(),
            blogpost.getContent(),
            blogpost.getCreatedAt(),
            categoryNames
        );
    }
}
