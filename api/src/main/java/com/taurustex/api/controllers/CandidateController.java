package com.taurustex.api.controllers;

import com.taurustex.api.dtos.CandidateDto;
import com.taurustex.api.dtos.CandidateStatisticsDto;
import com.taurustex.api.dtos.Pagination;
import com.taurustex.api.enums.Option;
import com.taurustex.api.enums.Serie;
import com.taurustex.api.enums.Status;
import com.taurustex.api.services.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/iai-concours-api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    /**
     * Récupère la liste paginée et filtrée des candidats.
     */
    @GetMapping
    //@PreAuthorize("hasAuthority('PERM_VIEW_CANDIDATE')")
    public ResponseEntity<Pagination<CandidateDto>> getCandidates(
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Serie serie,
            @RequestParam(required = false) Option option,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Pagination<CandidateDto> candidates = candidateService.getCandidates(numero, gender, status, serie, option, pageable);
        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/pending-candidate")
    public ResponseEntity<List<String>> getPendingCandidateNumber(){
        return ResponseEntity.ok(candidateService.getPendingCandidates());
    }

    /**
     * Mettre à jour les informations d'un candidat
     *
     * @param candidateId ID unique du candidat
     * @param candidateDto DTO contenant les données mises à jour
     * @return Candidat mis à jour (HTTP 200 OK)
     */
    @PutMapping("/{candidateId}")
    //@PreAuthorize("hasAuthority('PERM_UPDATE_CANDIDATE')")
    public ResponseEntity<CandidateDto> updateCandidate(
            @PathVariable String candidateId,
            @Valid @RequestBody CandidateDto candidateDto) {

        CandidateDto updatedCandidate = candidateService.updateCandidate(candidateId, candidateDto);
        return ResponseEntity.ok(updatedCandidate);
    }

    /**
     * Supprimer un candidat par son identifiant
     *
     * @param candidateId ID unique du candidat
     * @return HTTP 200 OK (si supprimé) ou HTTP 404 NOT FOUND (si inexistant)
     */
    @DeleteMapping("/{candidateId}")
    //@PreAuthorize("hasAuthority('PERM_DELETE_CANDIDATE')")
    public ResponseEntity<Void> deleteCandidate(@PathVariable String candidateId) {
        boolean deleted = candidateService.deleteCandidate(candidateId);

        if (deleted) {
            return ResponseEntity.ok().build(); // Ou HttpStatus.NO_CONTENT (204)
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Exporte les candidats au format CSV, Excel ou PDF.
     */
    @GetMapping("/export/{format}")
    //@PreAuthorize("hasAuthority('PERM_EXPORT')")
    public ResponseEntity<byte[]> exportCandidates(
            @PathVariable String format,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Serie serie,
            @RequestParam(required = false) Option option
    ) throws Exception {
        byte[] fileContent = candidateService.exportCandidates(format, numero, gender, status, serie, option);

        String filename = "candidats." + ("excel".equalsIgnoreCase(format) ? "xlsx" : format.toLowerCase());
        MediaType mediaType = resolveMediaType(format);


        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileContent);
    }

    private MediaType resolveMediaType(String format) {
        return switch (format.toLowerCase()) {
            case "csv" -> MediaType.parseMediaType("text/csv");
            case "excel" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "pdf" -> MediaType.APPLICATION_PDF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    @GetMapping("/statistics/global")
    //@PreAuthorize("hasAuthority('PERM_VIEW_DASHBOARD')")
    public ResponseEntity<CandidateStatisticsDto> getGlobalCandidateStatistics() {
        CandidateStatisticsDto statistics = candidateService.getGlobalCandidateStatistics();
        return ResponseEntity.ok(statistics);
    }

    // =========================================================================
    // 2. ENDPOINT STATISTIQUES FILTRÉES
    // GET /api/v1/candidates/statistics?numero=...&gender=...&status=...
    // =========================================================================
    @GetMapping("/statistics")
    //@PreAuthorize("hasAuthority('PERM_VIEW_DASHBOARD')")
    public ResponseEntity<CandidateStatisticsDto> getCandidateStatistics(
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Serie serie,
            @RequestParam(required = false) Option option
    ) {
        CandidateStatisticsDto statistics = candidateService.getCandidateStatistics(
                numero, gender, status, serie, option
        );
        return ResponseEntity.ok(statistics);
    }
}