package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
