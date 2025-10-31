package com.example.cms.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Blogpost {
    private Long id;
    private Long currentVersion;
    private List<Long> versionIds = new ArrayList<>();
}
