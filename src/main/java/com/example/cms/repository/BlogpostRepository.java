package com.example.cms.repository;

import com.example.cms.model.Blogpost;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class BlogpostRepository {
    private final Map<Long, Blogpost> blogposts = new HashMap<>();
    private Long nextId = 1L;

    public List<Blogpost> findAll() {
        return new ArrayList<>(blogposts.values());
    }

    public Optional<Blogpost> findById(final Long id) {
        return Optional.ofNullable(blogposts.get(id));
    }

    public Blogpost save(final Blogpost blogpost) {
        if (blogpost.getId() == null) {
            blogpost.setId(nextId);
            nextId++;
        }
        blogposts.put(blogpost.getId(), blogpost);
        return blogpost;
    }

    public void deleteById(final Long id) {
        blogposts.remove(id);
    }

}
