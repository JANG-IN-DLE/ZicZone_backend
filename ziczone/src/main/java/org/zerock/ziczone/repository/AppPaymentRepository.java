package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.ziczone.domain.AppPayment;
import org.zerock.ziczone.domain.member.PersonalUser;

import java.util.List;
import java.util.Optional;

public interface AppPaymentRepository extends JpaRepository<AppPayment, Long> {

    //특정 사용자의 berryBucket 값의 총합
    @Query("SELECT SUM(a.berryBucket) FROM AppPayment a WHERE a.personalUser = :userId")
    Optional<Long> findTotalBerryBuketByUserId(Long userId);

    //특정 사용자의 모든 AppPayment 기록
    @Query("SELECT a FROM AppPayment a WHERE a.personalUser.user.userId = :userId")
    List<AppPayment> findByUserId(Long userId);
}
