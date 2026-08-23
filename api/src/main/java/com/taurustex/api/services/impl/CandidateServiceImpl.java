package com.taurustex.api.services.impl;

import com.taurustex.api.annotations.NotifyClients;
import com.taurustex.api.dtos.*;
import com.taurustex.api.enums.Option;
import com.taurustex.api.enums.Serie;
import com.taurustex.api.enums.Status;
import com.taurustex.api.exception.ResourceNotFoundException;
import com.taurustex.api.mappers.CandidateExportMapper;
import com.taurustex.api.mappers.CandidateMapper;
import com.taurustex.api.models.Candidate;
import com.taurustex.api.models.Payment;
import com.taurustex.api.repositories.CandidateRepository;
import com.taurustex.api.repositories.PaymentRepository;
import com.taurustex.api.services.CandidateService;
import com.taurustex.api.specifications.CandidateSpecification;
import com.taurustex.api.tools.excel.ExcelCsvExportService;
import com.taurustex.api.tools.files.StorageService;
import com.taurustex.api.tools.jasper.CandidateReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;
    private final CandidateExportMapper candidateExportMapper;
    private final ExcelCsvExportService excelCsvExportService;
    private final CandidateReportService candidateReportService;
    private final StorageService storageService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public Pagination<CandidateDto> getCandidates(
            String numero,
            String gender,
            Status status,
            Serie serie,
            Option option,
            Pageable pageable
    ) {
        Sort defaultSort = Sort.by(Sort.Direction.ASC, "lastName")
                .and(Sort.by(Sort.Direction.ASC, "firstName"))
                .and(Sort.by(Sort.Direction.ASC, "birthDate"));

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().isSorted() ? pageable.getSort() : defaultSort
        );

        Specification<Candidate> spec = CandidateSpecification.filterCandidates(numero, gender, status, serie, option);
        Page<Candidate> candidatePage = candidateRepository.findAll(spec, sortedPageable);

        return Pagination.of(candidatePage.map(candidateMapper::toDto));
    }

    @Override
    @NotifyClients(topic = "/topic/candidates")
    public CandidateDto updateCandidate(String candidateId, CandidateDto candidate){

        Candidate old = candidateRepository.findById(candidateId).orElseThrow(
                ()-> new ResourceNotFoundException("Ce candidat n'existe pas")
        );

        old.setFirstName(candidate.getFirstName());
        old.setLastName(candidate.getLastName());
        old.setBirthDate(candidate.getBirthDate());
        old.setGender(candidate.getGender());
        old.setNationality(candidate.getNationality());
        old.setOption(candidate.getOption());
        old.setEmail(candidate.getEmail());
        old.setEnrolmentDate(candidate.getEnrolmentDate());
        old.setSerie(candidate.getSerie());
        old.setTutorPhone(candidate.getTutorPhone());
        old.setPhone(candidate.getPhone());
        return candidateMapper.toDto(candidateRepository.save(old));
    }

    @Override
    @NotifyClients(topic = "/topic/candidates")
    public boolean deleteCandidate(String candidateId){
        Candidate old = candidateRepository.findById(candidateId).orElseThrow(
                ()-> new ResourceNotFoundException("Ce candidat n'existe pas")
        );
        if (old.getPayment() != null) {
            paymentRepository.delete(old.getPayment());
        }
        candidateRepository.delete(old);
        return true;
    }

    @Override
    public List<String> getPendingCandidates() {
        return candidateRepository.findAllByStatus_RegisteredOnlyOrStatus_Pending();
    }

    @Override
    public byte[] exportCandidates(String format, String numero, String gender, Status status, Serie serie, Option option) throws Exception {

        Sort defaultSort = Sort.by(Sort.Direction.ASC, "lastName")
                .and(Sort.by(Sort.Direction.ASC, "firstName"))
                .and(Sort.by(Sort.Direction.ASC, "birthDate"));

        Specification<Candidate> spec = CandidateSpecification.filterCandidates(numero, gender, status, serie, option);

        List<Candidate> candidates = candidateRepository.findAll(spec, defaultSort);
        List<CandidateExportDto> cands = candidateExportMapper.toDto(candidates);

        // Compteurs distincts pour le cas où l'export est sur TOUT
        int countIngt = 1;
        int countIngc = 1;

        for (int i = 0; i < cands.size(); i++) {
            CandidateExportDto cand = cands.get(i);

            boolean isIngt = cand.getOption() != null && (
                    Option.WORKS_ENGINEERING.name().equalsIgnoreCase(cand.getOption()) ||
                            cand.getOption().toLowerCase().contains("travaux")
            );

            if (option == null) {
                // Export sur TOUT : numérotation commune globale sur 4 caractères
                cand.setNumeroTable(String.format("%04d", i + 1));
            } else {
                // Export filtré : numérotation distincte sur 3 caractères selon le type
                int sequenceNumber = isIngt ? countIngt++ : countIngc++;
                cand.setNumeroTable(String.format("%03d", sequenceNumber));
            }
        }

        CandidateReport report = new CandidateReport();
        report.setYear("2026-2027");
        report.setCandidates(cands);

        report.setLeftLogo(storageService.readAsStream("iai-logo.png"));
        report.setRightLogo(storageService.readAsStream("iai-logo.png"));

        if (Option.WORKS_ENGINEERING.equals(option)) {
            report.setTitle("CONCOURS D'ENTRÉE À L'IAI");
            report.setDescription("Liste des candidats - Cycle Ingénieur des Travaux Informatiques (INGT)");
        } else if (Option.DESIGN_ENGINEERING.equals(option)) {
            report.setTitle("CONCOURS D'ENTRÉE À L'IAI");
            report.setDescription("Liste des candidats - Cycle Ingénieur de Conception (INGC)");
        } else {
            report.setTitle("LISTE DES CANDIDATS");
            report.setDescription("Toutes options confondues");
        }

        return switch (format) {
            case "csv" -> excelCsvExportService.exportToCsv(cands);
            case "excel" -> excelCsvExportService.exportToExcel(cands);
            case "pdf" -> candidateReportService.generateReportPdf(report);
            default -> throw new IllegalStateException("Unexpected value: " + format);
        };
    }
    // =========================================================================
    // 1. STATISTIQUES GLOBALES (Sans aucun filtre)
    // =========================================================================
    @Transactional(readOnly = true)
    @Override
    public CandidateStatisticsDto getGlobalCandidateStatistics() {
        List<Candidate> allCandidates = candidateRepository.findAll();
        return computeStatistics(allCandidates);
    }

    // =========================================================================
    // 2. STATISTIQUES FILTRÉES (Selon les critères de recherche)
    // =========================================================================
    @Transactional(readOnly = true)
    @Override
    public CandidateStatisticsDto getCandidateStatistics(
            String numero,
            String gender,
            Status status,
            Serie serie,
            Option option
    ) {
        Specification<Candidate> spec = CandidateSpecification.filterCandidates(numero, gender, status, serie, option);
        List<Candidate> filteredCandidates = candidateRepository.findAll(spec);
        return computeStatistics(filteredCandidates);
    }

    // =========================================================================
    // Méthode utilitaire de calcul commune
    // =========================================================================
    private CandidateStatisticsDto computeStatistics(List<Candidate> candidates) {
        // 1. Calcul des totaux globaux
        CandidateStatisticsDto baseStats = computeStatisticsBase(candidates);

        // 2. Extraction de toutes les dates uniques (Uniquement EnrolmentDate OU PaymentDate)
        List<LocalDate> allDates = candidates.stream()
                .flatMap(c -> Stream.of(
                        c.getEnrolmentDate(),
                        c.getPayment() != null ? c.getPayment().getPaymentDate() : null
                ))
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder()) // Tri décroissant (le plus récent en premier)
                .toList();

        // 3. Calcul des statistiques par jour
        List<CandidateStatisticsDto.CandidateDayStatisticsDto> dailyStats = allDates.stream()
                .map(date -> {
                    // Candidats inscrits à cette date spécifique (basé strictement sur enrolmentDate ou paymentDate)
                    List<Candidate> enrolledOnDate = candidates.stream()
                            .filter(c -> date.equals(getEnrolmentDateOrFallback(c)))
                            .toList();

                    CandidateStatisticsDto dayBaseStats = computeStatisticsBase(enrolledOnDate);

                    // Calcul du montant réellement encaissé À CETTE DATE (basé uniquement sur paymentDate)
                    BigDecimal dailyAmount = candidates.stream()
                            .map(Candidate::getPayment)
                            .filter(p -> p != null && date.equals(p.getPaymentDate()) && p.getAmount() != null)
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    CandidateStatisticsDto.CandidateDayStatisticsDto dayDto = new CandidateStatisticsDto.CandidateDayStatisticsDto();
                    dayDto.setDate(date);
                    dayDto.setTotalStudents(dayBaseStats.getTotalStudents());
                    dayDto.setTotalFemaleStudents(dayBaseStats.getTotalFemaleStudents());
                    dayDto.setTotalMaleStudents(dayBaseStats.getTotalMaleStudents());
                    dayDto.setTotalFemaleIngt(dayBaseStats.getTotalFemaleIngt());
                    dayDto.setTotalMaleIngt(dayBaseStats.getTotalMaleIngt());
                    dayDto.setTotalFemaleIngc(dayBaseStats.getTotalFemaleIngc());
                    dayDto.setTotalMaleIngc(dayBaseStats.getTotalMaleIngc());
                    dayDto.setTotalAmount(dailyAmount);

                    return dayDto;
                })
                .toList();

        // 4. Assemblage du DTO final
        return CandidateStatisticsDto.builder()
                .totalStudents(baseStats.getTotalStudents())
                .totalFemaleStudents(baseStats.getTotalFemaleStudents())
                .totalMaleStudents(baseStats.getTotalMaleStudents())
                .totalFemaleIngt(baseStats.getTotalFemaleIngt())
                .totalMaleIngt(baseStats.getTotalMaleIngt())
                .totalFemaleIngc(baseStats.getTotalFemaleIngc())
                .totalMaleIngc(baseStats.getTotalMaleIngc())
                .totalAmount(baseStats.getTotalAmount())
                .darlingStats(dailyStats)
                .build();
    }

    /**
     * Helper privé pour évaluer la date d'inscription basée uniquement sur enrolmentDate ou paymentDate
     */
    private LocalDate getEnrolmentDateOrFallback(Candidate c) {
        if (c.getEnrolmentDate() != null) {
            return c.getEnrolmentDate();
        }
        if (c.getPayment() != null && c.getPayment().getPaymentDate() != null) {
            return c.getPayment().getPaymentDate();
        }
        return null;
    }

    /**
     * Helper privé de calcul des totaux de base
     */
    private CandidateStatisticsDto computeStatisticsBase(List<Candidate> candidates) {
        long totalStudents = candidates.size();

        long totalFemale = candidates.stream()
                .filter(c -> "F".equalsIgnoreCase(c.getGender()))
                .count();

        long totalMale = candidates.stream()
                .filter(c -> "M".equalsIgnoreCase(c.getGender()))
                .count();

        long totalFemaleIngt = candidates.stream()
                .filter(c -> Option.WORKS_ENGINEERING.equals(c.getOption()) && "F".equalsIgnoreCase(c.getGender()))
                .count();

        long totalMaleIngt = candidates.stream()
                .filter(c -> Option.WORKS_ENGINEERING.equals(c.getOption()) && "M".equalsIgnoreCase(c.getGender()))
                .count();

        long totalFemaleIngc = candidates.stream()
                .filter(c -> Option.DESIGN_ENGINEERING.equals(c.getOption()) && "F".equalsIgnoreCase(c.getGender()))
                .count();

        long totalMaleIngc = candidates.stream()
                .filter(c -> Option.DESIGN_ENGINEERING.equals(c.getOption()) && "M".equalsIgnoreCase(c.getGender()))
                .count();

        BigDecimal totalAmount = candidates.stream()
                .map(Candidate::getPayment)
                .filter(p -> p != null && p.getAmount() != null)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CandidateStatisticsDto.builder()
                .totalStudents(totalStudents)
                .totalFemaleStudents(totalFemale)
                .totalMaleStudents(totalMale)
                .totalFemaleIngt(totalFemaleIngt)
                .totalMaleIngt(totalMaleIngt)
                .totalFemaleIngc(totalFemaleIngc)
                .totalMaleIngc(totalMaleIngc)
                .totalAmount(totalAmount)
                .build();
    }
}