package com.taurustex.api.services;

import com.taurustex.api.dtos.CandidateDto;
import com.taurustex.api.dtos.CandidateStatisticsDto;
import com.taurustex.api.dtos.Pagination;
import com.taurustex.api.enums.Option;
import com.taurustex.api.enums.Serie;
import com.taurustex.api.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

public interface CandidateService {
    Pagination<CandidateDto> getCandidates(
            String numero,
            String gender,
            Status status,
            Serie serie,
            Option option,
            Pageable pageable
    );

    CandidateDto updateCandidate(String candidateId, CandidateDto candidate);

    boolean deleteCandidate(String candidateId);

    List<String> getPendingCandidates();

    byte[] exportCandidates(
            String format,
            String numero,
            String gender,
            Status status,
            Serie serie,
            Option option
    ) throws Exception;


    // =========================================================================
    // 1. STATISTIQUES GLOBALES (Sans aucun filtre)
    // =========================================================================
    @Transactional(readOnly = true)
    CandidateStatisticsDto getGlobalCandidateStatistics();

    // =========================================================================
    // 2. STATISTIQUES FILTRÉES (Selon les critères de recherche)
    // =========================================================================
    @Transactional(readOnly = true)
    CandidateStatisticsDto getCandidateStatistics(
            String numero,
            String gender,
            Status status,
            Serie serie,
            Option option
    );
}
