package com.example.cms.model;

import java.util.ArrayList;
import java.util.List;

public class Blogpost {
    private Long id;
    private Long currentVersion;
    private List<Long> versionIds = new ArrayList<>();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Long currentVersion) { this.currentVersion = currentVersion; }
    public List<Long> getVersionIds() { return versionIds; }
    public void setVersionIds(List<Long> versionIds) { this.versionIds = versionIds; }
}
