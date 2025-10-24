package com.albymens.note_app.dto;

import java.util.List;

public class NoteDto {
    private Long id;
    private String title;
    private String content;
    private List<String> tags;
    private String username;

    public NoteDto(Long id, String title, String content, List<String> tags, String username) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
