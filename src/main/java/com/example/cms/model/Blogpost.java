package com.example.cms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Blogpost {
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private List<Long> categories;

    // Constructors
    public Blogpost() {}
    public Blogpost(Long id, String title, String content, String author, List<Long> categories) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.categories = categories != null ? categories : new ArrayList<>();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Long> getCategories() { return categories; }
    public void setCategories(List<Long> categories) { this.categories = categories; }
}
