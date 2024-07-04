package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.PayHistory;

public interface PayHistoryRepository extends JpaRepository<PayHistory, Long> {
    // PayHistory에서 BuyerId와 SellerId가 있는지 체크
    boolean existsByBuyerIdAndSellerId(Long buyerId, Long sellerId);
}
