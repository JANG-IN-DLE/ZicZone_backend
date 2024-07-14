package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import org.zerock.ziczone.domain.application.Portfolio;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByResume_ResumeId(Long resumeId);

    void deleteByResumeId(Long resumeId);
}
