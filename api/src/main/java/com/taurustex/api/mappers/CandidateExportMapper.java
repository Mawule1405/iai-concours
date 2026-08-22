package com.taurustex.api.mappers;

import com.taurustex.api.dtos.CandidateExportDto;
import com.taurustex.api.enums.Option;
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
        dto.setSerie(candidate.getSerie().name());

        if(Option.WORKS_ENGINEERING.equals(candidate.getOption())) {
            dto.setOption("INGT");
        }else{
            dto.setOption("INGC");
        }

        dto.setNumero(candidate.getNumero());
        if(candidate.getPayment() != null) {
            dto.setAmount(candidate.getPayment().getAmount());
            dto.setNumberOfTransactions(candidate.getPayment().getNumberOfTransactions());
            dto.setPaymentDate(candidate.getPayment().getPaymentDate());
            dto.setTransferPhone(candidate.getPayment().getTransferPhone());
            dto.setTransferHour(candidate.getPayment().getTransferHour());
        }
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
