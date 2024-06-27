package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
