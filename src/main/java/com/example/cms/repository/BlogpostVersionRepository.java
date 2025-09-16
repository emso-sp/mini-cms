package com.example.cms.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.cms.model.BlogpostVersion;

@Repository
public class BlogpostVersionRepository {
    private final Map<Long, BlogpostVersion> versions = new HashMap<>();
    private Long nextId = 1L;

    public BlogpostVersion save(final BlogpostVersion version) {
        if (version.getId() == null) {
            version.setId(nextId);
            nextId++;
        }
        versions.put(version.getId(), version);
        return version;
    }

    public Optional<BlogpostVersion> findById(final Long id) {
        return Optional.ofNullable(versions.get(id));
    }

    public List<BlogpostVersion> findByBlogpostId(final Long blogpostId) {
        List<BlogpostVersion> list = new ArrayList<>();
        for (BlogpostVersion version : versions.values()) {
            if (version.getBlogpostId().equals(blogpostId)) {
                list.add(version);
            }
        }
        return list;
    }

    public void deleteById(final Long id) {
        versions.remove(id);
    }

    public void deleteByBlogpostId(final Long blogpostId) {
        for (BlogpostVersion version : versions.values()) {
            if (version.getBlogpostId().equals(blogpostId)) {
                versions.remove(version.getId());
            }
        }
    }
    
}
