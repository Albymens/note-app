package com.albymens.note_app.service;

import com.albymens.note_app.config.SpringConfiguration;
import com.albymens.note_app.dto.NoteDto;
import com.albymens.note_app.model.Note;
import com.albymens.note_app.model.User;
import com.albymens.note_app.repository.NoteRepository;
import com.albymens.note_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Import(SpringConfiguration.class)
@ActiveProfiles("test")
public class NoteServiceIntegrationTest {
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoteService noteService;

    private Note note1, note2, note3, deleteNote;
    private User testuser;

    @BeforeEach
    public void setUp(){
        noteRepository.deleteAll();
        userRepository.deleteAll();

        testuser = new User();
        note1 = new Note();
        note2 = new Note();
        note3 = new Note();

        testuser.setUsername("Alby");
        testuser.setPassword("uytw4321");
        testuser.setEmail("alby@gmail.com");
        userRepository.save(testuser);

        note1.setTitle("Spring Boot Framework");
        note1.setContent("Complete guide to Spring Boot Framework");
        note1.setTags(List.of("Dependency Injection", "Starter Guide", "Annotations"));
        note1.setUser(testuser);
        note1.setCreatedAt(Instant.now());
        note1.setUpdatedAt(Instant.now());

        note2.setTitle("Spring Security Basics");
        note2.setContent("Understanding Spring Security configuration");
        note2.setTags(List.of("Spring security", "Jwt"));
        note2.setUser(testuser);
        note2.setCreatedAt(Instant.now());
        note2.setUpdatedAt(Instant.now());

        note3.setTitle("Database Management");
        note3.setContent("Complete tutorials on Database Management");
        note3.setTags(List.of("Beginner", "Advanced"));
        note3.setUser(testuser);
        note3.setCreatedAt(Instant.now());
        note3.setUpdatedAt(Instant.now());

        noteRepository.saveAll(List.of(note1, note2, note3));
    }

    @Test
    @DisplayName("Should find notes by search term in title or content")
    void shouldFindNotesBySearchTerm() {
        String searchTerm = "Spring";
        Pageable pageable = PageRequest.of(0, 10);

        Page<NoteDto> result = noteService.searchNotes(testuser, null, searchTerm, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(NoteDto::getTitle)
                .containsExactlyInAnyOrder("Spring Boot Framework", "Spring Security Basics");
    }

    @Test
    @DisplayName("Should filter notes by tags")
    void shouldFilterNotesByTags() {
        List<String> tags = List.of("Advanced");
        Pageable pageable = PageRequest.of(0, 10);

        Page<NoteDto> result = noteService.searchNotes(testuser, tags, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(NoteDto::getTitle)
                .containsExactly("Database Management");
    }

}
