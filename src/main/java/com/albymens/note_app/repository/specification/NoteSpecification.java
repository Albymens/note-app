package com.albymens.note_app.repository.specification;

import com.albymens.note_app.model.Note;
import com.albymens.note_app.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NoteSpecification {
    public static Specification<Note> belongsToUser(User user){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Note>   isNotDeleted(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt"));
    }

    public static Specification<Note>  containSearchTerm(String searchTerm){
        return (root, query, criteriaBuilder) -> {
            if(searchTerm == null || searchTerm.trim().isEmpty()){
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + searchTerm + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), pattern)
            );
        };
    }

    public static Specification<Note> hasAnyTags(List<String> tags){
        return (root, query, criteriaBuilder) -> {
            if (tags == null || tags.isEmpty()){
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            for (String tag : tags){
                String normalizedTag = tag.trim().toLowerCase();

                // More precise patterns for CSV
                String[] patterns = {
                        "%," + normalizedTag + ",%",  // tag in middle
                        normalizedTag + ",%",         // tag at start
                        "%," + normalizedTag,         // tag at end
                        normalizedTag                 // only tag
                };

                List<Predicate> patternPredicates = Arrays.stream(patterns)
                        .map(pattern -> criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("tags")),
                                pattern
                        ))
                        .collect(Collectors.toList());

                predicates.add(criteriaBuilder.or(
                        patternPredicates.toArray(new Predicate[0])
                ));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };

    }
}
