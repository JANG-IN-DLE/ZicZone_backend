package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.ziczone.domain.PayHistory;
import org.zerock.ziczone.domain.payment.Payment;
import org.zerock.ziczone.dto.mypage.PayHistoryDTO;

import java.util.List;
import java.util.Optional;

public interface PayHistoryRepository extends JpaRepository<PayHistory, Long> {
    // PayHistory에서 BuyerId와 SellerId가 있는지 체크
    boolean existsByBuyerIdAndSellerId(Long buyerId, Long sellerId);

    List<PayHistory> findByBuyerId(Long personalId);

}
