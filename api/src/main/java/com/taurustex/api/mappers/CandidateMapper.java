package com.taurustex.api.mappers;

import com.taurustex.api.dtos.CandidateDto;
import com.taurustex.api.models.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateMapper {
    @Mapping(target="paymentId", source = "payment.id")
    CandidateDto toDto(Candidate candidate);
    Candidate toEntity(CandidateDto candidateDto);
}
