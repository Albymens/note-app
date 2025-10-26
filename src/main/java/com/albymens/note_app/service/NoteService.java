package com.albymens.note_app.service;

import com.albymens.note_app.dto.NoteDto;
import com.albymens.note_app.exception.DuplicateResourceException;
import com.albymens.note_app.exception.ResourceNotFoundException;
import com.albymens.note_app.model.Note;
import com.albymens.note_app.model.User;
import com.albymens.note_app.repository.NoteRepository;
import com.albymens.note_app.model.converter.TagConverter;
import com.albymens.note_app.repository.specification.NoteSpecification;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Transactional
public class NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    UserService userService;

    public NoteDto createNote(Note request, String username){

        User user = userService.findByUsername(username).orElseThrow(()->{
            throw new ResourceNotFoundException("User not found");
        });


       if(request.getId() != null && noteRepository.findById(request.getId()).isPresent()){
           logger.error("Note already exists with id: {}", request.getId());
           throw new DuplicateResourceException("Note already exists with id: " + request.getId());
       }

        Note note = new Note();
       note.setUser(user);
       note.setTags(TagConverter.normalizeTags(request.getTags()));
       note.setContent(request.getContent());
       note.setTitle(request.getTitle());

       Note savedNote = noteRepository.save(note);
       logger.info("New note {} created at {}", savedNote.getId(), savedNote.getCreatedAt());

       return toDto(savedNote);
    }

    public NoteDto getNoteById(Long noteId){
        Note note = noteRepository.findById(noteId).orElseThrow(
                ()-> {
                    logger.error("Note with id: {}, not found", noteId);
                    return new ResourceNotFoundException("Note with id: {}, not found" + noteId);
                });
        return toDto(note);

    }

    public NoteDto updateNote(Long id, Note note){
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
        return toDto(savedNote);
    }

    public void deleteNote(Long noteId){
        Note note = noteRepository.findById(noteId).orElseThrow(() -> {
            logger.error("Note not found with id: {}", noteId);
            return new ResourceNotFoundException("Note not found with id: " + noteId);
        });

        note.setDeletedAt(Instant.now());
        noteRepository.save(note);
        logger.info("Note with id: {}, deleted successfully", noteId);
    }

    public void restoreNote(Long noteId){
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

    public List<NoteDto> findAllActiveNotes(){
        List<Note> notes = noteRepository.findAll();
        return notes.stream().map(this::toDto).toList();
    }

    private NoteDto toDto(Note note){
        return new NoteDto(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getTags(),
                note.getUser().getUsername(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }

    private Specification<Note> buildSearchSpecification(User user, List<String> tags, String searchTerm){
        Specification<Note> spec = NoteSpecification.isNotDeleted();

       if(null != user) spec = spec.and(NoteSpecification.belongsToUser(user));
       if(null != tags && !tags.isEmpty()) spec = spec.and(NoteSpecification.hasAnyTags(tags));
       if(null != searchTerm) spec = spec.and(NoteSpecification.containSearchTerm(searchTerm));

       return spec;

    }

    public Page<NoteDto> searchNotes(User user, List<String> tags, String searchTerm, Pageable page){
        logger.info("Searching notes for user: {}", user != null ? user.getUsername() : "null");

        Specification<Note> spec = buildSearchSpecification(user, tags, searchTerm);
        Page<Note> notes = noteRepository.findAll(spec, page);

        logger.info("Found {} notes out of {} total", notes.getNumberOfElements(), notes.getTotalElements());

        return notes.map(this::toDto);
    }
}
