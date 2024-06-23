package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
