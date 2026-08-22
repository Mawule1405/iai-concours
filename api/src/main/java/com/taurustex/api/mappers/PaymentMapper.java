package com.taurustex.api.mappers;

import com.taurustex.api.dtos.PaymentDto;
import com.taurustex.api.models.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {


    PaymentDto toDto(Payment payment);
    Payment toEntity(PaymentDto paymentDto);

}
