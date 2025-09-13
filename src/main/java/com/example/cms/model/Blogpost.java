package com.example.cms.model;

import java.time.LocalDateTime;

public class Blogpost {
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private Long[] categories;

    // Constructors
    public Blogpost() {}
    public Blogpost(Long id, String title, String content, String author, LocalDateTime createdAt, Long[] categories) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.categories = categories;
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
    public Long[] getCategories() { return categories; }
    public void setCategories(Long[] categories) { this.categories = categories; }
}
