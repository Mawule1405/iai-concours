package com.taurustex.api.repositories;

import com.taurustex.api.basis.BaseRepository;
import com.taurustex.api.models.Payment;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends BaseRepository<Payment,String> {
}
