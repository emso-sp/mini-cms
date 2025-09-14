package com.example.cms.dto;
import java.util.List;

public record PostRequest(String title, String author, String content, List<Long> categoryIds) {}