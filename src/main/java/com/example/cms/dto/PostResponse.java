package com.example.cms.dto;

import java.util.List;
import java.time.LocalDateTime;

public record PostResponse(Long id, String title, String author, String content, LocalDateTime createdAt, List<String> categories) {}
