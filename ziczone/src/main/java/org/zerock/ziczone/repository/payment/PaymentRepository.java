package org.zerock.ziczone.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.payment.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 특정 사용자의 모든 성공적인 결제 내역을 조회
    @Query("SELECT p FROM Payment p WHERE p.personalUser.personalId = :personalId AND p.payState = 'SUCCESS'")
    Optional<List<Payment>>  findAllSuccessfulPaymentsByPersonalId(Long personalId);

    @Query("SELECT SUM(p.berryPoint) FROM Payment p WHERE p.personalUser.personalId = :personalId AND p.payState = org.zerock.ziczone.domain.payment.PayState.SUCCESS")
    Optional<Integer> findTotalBerryPointsByPersonalId(Long personalId);



    Payment findByPersonalUser_PersonalId(Long personalId);

    Payment findByPersonalUser(PersonalUser personalUser);

}
