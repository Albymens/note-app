package com.albymens.note_app.repository.specification;

import com.albymens.note_app.model.Note;
import com.albymens.note_app.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class NoteSpecification {
    public static Specification<Note> belogToUser(User user){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Note> isNotDeleted(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("deletedAt"));
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
                String normalizeTag = tag.trim().toLowerCase();
                predicates.add(criteriaBuilder.isMember(normalizeTag, root.get("tags")));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };

    }
}
