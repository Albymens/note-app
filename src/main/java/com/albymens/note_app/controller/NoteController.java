package com.albymens.note_app.controller;

import com.albymens.note_app.dto.ApiResult;
import com.albymens.note_app.model.Note;
import com.albymens.note_app.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    NoteService noteService;

    @PostMapping("")
    public ResponseEntity<ApiResult> createNote(@RequestBody @Valid Note note,
                                                @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResult(true, "Notes created successfully", noteService.createNote(note, userDetails.getUsername()))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResult> getNote(@PathVariable Long id){
        return ResponseEntity.ok(new ApiResult(
                true, "Note retrieved successfully", noteService.getNoteById(id)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResult> updateNote(@PathVariable Long id, @RequestBody Note note){
        return ResponseEntity.ok(new ApiResult(
                true, "Note updated successfully", noteService.updateNote(id, note)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResult> deleteNote(@PathVariable Long id){
        noteService.deleteNote(id);
        return ResponseEntity.ok(new ApiResult(
                true, "Note deleted successfully", null
        ));
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResult> restoreDeletedNote(@PathVariable Long id){
        noteService.restoreNote(id);
        return ResponseEntity.ok(new ApiResult(
                true, "Note restored successfully", null
        ));
    }

}
