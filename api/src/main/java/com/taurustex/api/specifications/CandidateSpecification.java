package com.taurustex.api.specifications;


import com.taurustex.api.enums.Option;
import com.taurustex.api.enums.Serie;
import com.taurustex.api.enums.Status;
import com.taurustex.api.models.Candidate; // Votre entité Candidate
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CandidateSpecification {

    public static Specification<Candidate> filterCandidates(
            String numero,
            String gender,
            Status status,
            Serie serie,
            Option option
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(numero)) {
                // Recherche partielle sur le numéro (ou numeroTable selon votre entité)
                predicates.add(cb.like(cb.lower(root.get("numeroTable")), "%" + numero.toLowerCase() + "%"));
            }

            if (StringUtils.hasText(gender)) {
                predicates.add(cb.equal(cb.lower(root.get("gender")), gender.toLowerCase()));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (serie != null) {
                predicates.add(cb.equal(root.get("serie"), serie));
            }

            if (option != null) {
                predicates.add(cb.equal(root.get("option"), option));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
