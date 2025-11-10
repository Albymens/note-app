package com.albymens.note_app.controller;

import com.albymens.note_app.dto.ApiResult;
import com.albymens.note_app.dto.NoteDto;
import com.albymens.note_app.dto.PageResponse;
import com.albymens.note_app.model.Note;
import com.albymens.note_app.model.User;
import com.albymens.note_app.service.NoteService;
import com.albymens.note_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Note Management",
        description = "Endpoints for managing notes. Allows users to create, edit, delete, search, and filter their personal notes.")
public class NoteController {
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);


    @Autowired
    NoteService noteService;
    @Autowired
    UserService userService;

    @Operation(summary = "Create a new note",
    description = "Allow an authenticated user to create a new note with title, content and tags.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Note created successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            })
    @PostMapping("")
    public ResponseEntity<ApiResult> createNote(@RequestBody @Valid Note note,
                                                @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResult(true, "Notes created successfully", noteService.createNote(note, userDetails.getUsername()))
        );
    }

    @Operation(
            summary = "Get a note",
            description = "Retrieve current user note",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Note retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult> getNote(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResult(
                true, "Note retrieved successfully", noteService.getNoteById(id)
        ));
    }

    @Operation(
            summary = "Update a Note",
            description = "Update an existing note's title, content and tags for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Note updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Note not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResult> updateNote(@PathVariable Long id, @RequestBody Note note){
        return ResponseEntity.ok(new ApiResult(
                true, "Note updated successfully", noteService.updateNote(id, note)
        ));
    }

    @Operation(
            summary = "Delete a note",
            description = "Marks the note as deleted (Soft Delete). The note can later be restored using the restore endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Note deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Note not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResult> deleteNote(@PathVariable Long id){
        noteService.deleteNote(id);
        return ResponseEntity.ok(new ApiResult(
                true, "Note deleted successfully", null
        ));
    }

    @Operation(
            summary = "Restore a deleted Note",
            description = "Restores a previous deleted note for a user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Note restored successfully")
            }
    )
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResult> restoreDeletedNote(@PathVariable Long id){
        noteService.restoreNote(id);
        return ResponseEntity.ok(new ApiResult(
                true, "Note restored successfully", null
        ));
    }

    @Operation(
            summary = "Search notes",
            description = "Search notes by title, content, or tags. Supports pagination.",
            parameters = {
                    @Parameter(name = "searchTerm", description = "Keyword to search in title or content", example = "meeting"),
                    @Parameter(name = "tags", description = "List of tags to filter by", example = "['work','personal']"),
                    @Parameter(name = "page", description = "Page number (default = 0)", example = "0"),
                    @Parameter(name = "size", description = "Page size (default = 10)", example = "10")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notes retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters")
            }
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResult> searchNotes(
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsernameOrEmail(userDetails.getUsername());

        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<NoteDto> results = noteService.searchNotes(user, tags, searchTerm, pageable);
        logger.info("Results {}", results);
        PageResponse<NoteDto> pageResponse = new PageResponse<>(results);

        return ResponseEntity.ok(new ApiResult(true, "Notes retrieved successfully", pageResponse));
    }

    @Operation(
            summary = "Get All active notes",
            description = "Retrieves all active(non-deleted notes) belonging to the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notes retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "UnAuthorized")
            }
    )
    @GetMapping("")
    public ResponseEntity<ApiResult> getActiveNotes(){
        return ResponseEntity.ok(new ApiResult(
                true, "Notes retrieved successfully", noteService.findAllActiveNotes())
        );
    }


}
