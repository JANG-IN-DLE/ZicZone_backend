package org.zerock.ziczone.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.payment.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByPersonalUser_PersonalId(Long personalId);
}
