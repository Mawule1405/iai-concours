package com.taurustex.api.repositories;

import com.taurustex.api.basis.BaseRepository;
import com.taurustex.api.models.Candidate;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends BaseRepository<Candidate, String>, JpaSpecificationExecutor<Candidate> {
    Optional<Candidate> findByNumero(String numero);

    @Query("SELECT c.numero " +
            "FROM Candidate c " +
            "WHERE c.status IN ('PENDING', 'REGISTERED_ONLY')" +
            "ORDER BY c.numero")
    List<String> findAllByStatus_RegisteredOnlyOrStatus_Pending();
}
