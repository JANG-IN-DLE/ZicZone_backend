package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.JobPosition;

public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {
}
