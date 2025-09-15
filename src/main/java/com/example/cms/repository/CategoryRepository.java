package com.example.cms.repository;

import com.example.cms.model.Category;
import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository {
    private final Map<Long, Category> categories = new HashMap<>();
    private Long nextId = 1L;

    public List<Category> findAll() {
        return new ArrayList<>(categories.values());
    }

    public Optional<Category> findById(final Long id) {
        return Optional.ofNullable(categories.get(id));
    }

    public Category save(final Category category) {
        if (category.getId() == null) {
            category.setId(nextId);
            nextId++;
        }
        categories.put(category.getId(), category);
        return category;
    }

    public void deleteById(final Long id) {
        categories.remove(id);
    }

}
