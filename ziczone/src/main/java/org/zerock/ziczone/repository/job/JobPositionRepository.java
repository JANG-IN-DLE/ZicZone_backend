package org.zerock.ziczone.repository.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.job.JobPosition;

import java.util.List;

public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {
    List<JobPosition> findByPersonalUserPersonalId(Long personalId);
}
