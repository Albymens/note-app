package com.albymens.note_app.model;

import com.albymens.note_app.utils.TagConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Title is required")
    private String title;
    private String content;

    @Column(columnDefinition = "JSON")
    @Convert(converter = TagConverter.class)

    private List<String> tags;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    public void restore() {
        this.deletedAt = null;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    private void normalizeTags() {
        if (this.tags != null) {
            this.tags = new TagConverter().normalizeTags(this.tags);
        }
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        if (tag == null || tag.isBlank()) return;
        if (this.tags == null) this.tags = new ArrayList<>();
        String normalized = tag.trim().toLowerCase();
        if (!this.tags.contains(normalized)) {
            this.tags.add(normalized);
        }
    }


    public boolean hasTag(String tag) {
        if (this.tags == null || tag == null) return false;
        return this.tags.contains(tag.trim().toLowerCase());
    }
}
