package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.AppPayment;

public interface AppPaymentRepository extends JpaRepository<AppPayment, Long> {
}
