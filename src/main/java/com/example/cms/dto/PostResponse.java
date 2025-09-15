package com.example.cms.dto;

import com.example.cms.model.Status;

import java.util.List;
import java.time.LocalDateTime;

public record PostResponse(Long blogpostId, Integer versionNumber, String title, String author, String content, LocalDateTime createdAt, Status status, List<String> categories) {}
