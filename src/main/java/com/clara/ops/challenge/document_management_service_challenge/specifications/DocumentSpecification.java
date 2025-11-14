package com.clara.ops.challenge.document_management_service_challenge.specifications;

import com.clara.ops.challenge.document_management_service_challenge.entities.Document;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification {
  public static Specification<Document> withFilters(
      String userName, String documentName, List<String> tags) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (userName != null && !userName.isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.get("userName"), userName));
      }

      if (documentName != null && !documentName.isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.get("documentName"), documentName));
      }

      if (tags != null && !tags.isEmpty()) {
        for (String tag : tags) {
          predicates.add(criteriaBuilder.isMember(tag, root.get("tags")));
        }
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
