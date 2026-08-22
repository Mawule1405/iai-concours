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
                String pattern = "%" + numero.toLowerCase() + "%";

                Predicate searchByNumero = cb.like(cb.lower(root.get("numero")), pattern);
                Predicate searchByNumeroTable = cb.like(cb.lower(root.get("numeroTable")), pattern);
                Predicate searchByFirstName = cb.like(cb.lower(root.get("firstName")), pattern);
                Predicate searchByLastName = cb.like(cb.lower(root.get("lastName")), pattern);

                // Combine tous les prédicats avec un OR et ajoute le résultat global à la liste
                predicates.add(cb.or(searchByNumero, searchByNumeroTable, searchByFirstName, searchByLastName));
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
