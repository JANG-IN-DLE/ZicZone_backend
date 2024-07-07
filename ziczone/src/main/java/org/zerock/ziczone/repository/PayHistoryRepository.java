package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.PayHistory;

import java.util.List;
import java.util.Optional;

public interface PayHistoryRepository extends JpaRepository<PayHistory, Long> {
    // PayHistory에서 BuyerId와 SellerId가 있는지 체크
    boolean existsByBuyerIdAndSellerId(Long buyerId, Long sellerId);
    Optional<PayHistory> findByBuyerIdAndSellerId(Long buyerId, Long sellerId);
    // pickCards 조회할 때 결제여부 파악하기 위해서
    List<PayHistory> findBySellerIdAndBuyerId(Long sellerId, Long buyerId);
}
