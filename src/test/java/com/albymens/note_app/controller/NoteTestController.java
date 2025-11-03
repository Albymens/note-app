package com.albymens.note_app.controller;

import com.albymens.note_app.config.JwtAuthenticationFilter;
import com.albymens.note_app.dto.NoteDto;
import com.albymens.note_app.model.User;
import com.albymens.note_app.service.JwtService;
import com.albymens.note_app.service.NoteService;
import com.albymens.note_app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class NoteTestController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private UserService userService;

    private NoteDto note1;
    private NoteDto note2;
    private User testuser;

    @BeforeEach
    void setUp() {
        note1 = new NoteDto(1L, "Spring Boot Framework", "Complete guide to Spring Boot Framework",
                List.of("Dependency Injection", "Starter Guide", "Annotations"), "Alby", Instant.now(), Instant.now());

        note2 = new NoteDto(2L, "Spring Security Basics", "Understanding Spring Security configuration",
                List.of("Spring Security", "Jwt"), "Alby", Instant.now(), Instant.now());
        testuser = new User();
        testuser.setUsername("Alby");
        testuser.setEmail("alby@gmail.com");

    }

    @Test
    @DisplayName("Should return paginated notes for search term")
    @WithMockUser(username = "Alby")
    void shouldReturnNotesForSearchTerm() throws Exception {
        List<NoteDto> notes = List.of(note1, note2);
        given(userService.findByUsernameOrEmail("Alby")).willReturn(testuser);
        given(noteService.searchNotes(eq(testuser), any(), eq("Spring"), any()))
                .willReturn(new org.springframework.data.domain.PageImpl<>(notes));

        mockMvc.perform(get("/api/notes/search")
                        .param("searchTerm", "Spring") // ✅ match controller param
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Notes retrieved successfully"))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].title").value("Spring Boot Framework"));
    }
}
