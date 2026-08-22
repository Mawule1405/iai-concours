package com.taurustex.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateStatisticsDto {

    private Long totalStudents;
    private Long totalFemaleStudents;
    private Long totalMaleStudents;
    private Long totalFemaleIngt;
    private Long totalMaleIngt;
    private Long totalFemaleIngc;
    private Long totalMaleIngc;
    private BigDecimal totalAmount;

    @Builder.Default
    private List<CandidateDayStatisticsDto> darlingStats = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateDayStatisticsDto {
        private LocalDate date;
        private Long totalStudents;
        private Long totalFemaleStudents;
        private Long totalMaleStudents;
        private Long totalFemaleIngt;
        private Long totalMaleIngt;
        private Long totalFemaleIngc;
        private Long totalMaleIngc;
        private BigDecimal totalAmount;
    }
}