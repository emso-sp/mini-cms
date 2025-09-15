package com.example.cms.model;

import java.time.LocalDateTime;
import java.util.List;

public class BlogpostVersion {
    private Long id;
    private Long blogpostId;
    private Integer versionNumber;
    private String title;
    private String author;
    private String content;
    private Status status;
    private List<Long> categories;
    private LocalDateTime createdAt;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBlogpostId() { return blogpostId; }
    public void setBlogpostId(Long blogpostId) { this.blogpostId = blogpostId; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public List<Long> getCategories() { return categories; }
    public void setCategories(List<Long> categories) { this.categories = categories; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
