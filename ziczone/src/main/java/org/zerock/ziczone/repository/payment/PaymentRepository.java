package org.zerock.ziczone.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.ziczone.domain.payment.Payment;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 남은 포인트조회에 사용 미사용이지만 수정예정
    @Query("SELECT SUM(p.berryPoint) FROM Payment p WHERE p.payId = :userId")
    Optional<Long> findTotalBerryPointsByUserId(Long userId);

}
