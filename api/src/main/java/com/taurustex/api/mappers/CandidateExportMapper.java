package com.taurustex.api.mappers;

import com.taurustex.api.dtos.CandidateExportDto;
import com.taurustex.api.models.Candidate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CandidateExportMapper {

    public CandidateExportDto toDto(Candidate candidate) {
        CandidateExportDto dto = new CandidateExportDto();

        dto.setBirthDate(candidate.getBirthDate().toString());
        dto.setFirstName(candidate.getFirstName());
        dto.setLastName(candidate.getLastName());
        dto.setGender(candidate.getGender());
        dto.setNationality(candidate.getNationality());
        dto.setPhone(candidate.getPhone());
        dto.setEmail(candidate.getEmail());
        dto.setStatus(candidate.getStatus().name());
        dto.setSerie(candidate.getSerie().name());
        dto.setOption(candidate.getOption().name());
        dto.setNumero(candidate.getNumero());
        dto.setNumeroTable(candidate.getNumeroTable());
        return dto;
    }

    public List<CandidateExportDto> toDto(List<Candidate> candidates) {
        List<CandidateExportDto> dtos = new ArrayList<>();
        for (Candidate candidate : candidates) {
            dtos.add(toDto(candidate));
        }
        return dtos;
    }

}
