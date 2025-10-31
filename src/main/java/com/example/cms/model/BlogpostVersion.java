package com.example.cms.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
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
}
