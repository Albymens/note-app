package com.albymens.note_app.service;

import com.albymens.note_app.exception.DuplicateResourceException;
import com.albymens.note_app.exception.ResourceNotFoundException;
import com.albymens.note_app.model.Note;
import com.albymens.note_app.repository.NoteRepository;
import com.albymens.note_app.utils.TagConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
public class NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    @Autowired
    private NoteRepository noteRepository;

    public Note createNote(Note note){
       if(note.getId() != null && noteRepository.findById(note.getId()).isPresent()){
           logger.error("Note already exists with id: {}", note.getId());
           throw new DuplicateResourceException("Note already exists with id: " + note.getId());
       }

       note.setTags(TagConverter.normalizeTags(note.getTags()));

       Note savedNote = noteRepository.save(note);
       logger.info("New note {} created at {}", savedNote.getId(), savedNote.getCreatedAt());

       return savedNote;
    }

    public Note getNoteById(String noteId){
        return noteRepository.findById(noteId).orElseThrow(
                ()-> {
                    logger.error("Note with id: {}, not found", noteId);
                    return new ResourceNotFoundException("Note with id: {}, not found" + noteId);
                });

    }

    public Note updateNote(String id, Note note){
        Note existingNote = noteRepository.findById(id).orElseThrow(
                ()-> {
                    logger.error("Note not found with id: {}", id);
                    return new ResourceNotFoundException("Note not found with id: " + id);
                });

        if(StringUtils.hasText(note.getContent())){
            existingNote.setContent(note.getContent());
        }
        if(StringUtils.hasText(note.getTitle())){
            existingNote.setTitle(note.getTitle());
        }

        if(null !=  note.getTags()){
            existingNote.setTags(TagConverter.normalizeTags(note.getTags()));
        }

        Note savedNote = noteRepository.save(existingNote);

        logger.info("Note with id: {} updated successfully at {}", savedNote.getId(), savedNote.getUpdatedAt());
        return savedNote;
    }

    public void deleteNote(String noteId){
        Note note = noteRepository.findById(noteId).orElseThrow(() -> {
            logger.error("Note not found with id: {}", noteId);
            return new ResourceNotFoundException("Note not found with id: " + noteId);
        });

        note.setDeletedAt(Instant.now());
        noteRepository.save(note);
        logger.info("Note with id: {}, deleted successfully", noteId);
    }

    public void restoreNote(String noteId){
        Note note = noteRepository.findById(noteId).orElseThrow(()-> {
            logger.error("Note not found with id: {}", noteId);
            return new ResourceNotFoundException("Note not found with id: " + noteId);
        });

        if(null == note.getDeletedAt()){
            logger.warn("Note with id: {}, already restored", noteId);
        } else {
            note.setDeletedAt(null);
            logger.info("Note with id: {}, restored successfully", noteId);
        }
    }

    public List<Note> findAllActiveNotes(){
        return noteRepository.findByDeletedAtIsNull();
    }
}
