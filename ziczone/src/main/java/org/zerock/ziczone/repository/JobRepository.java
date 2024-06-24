package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
    Job findByJobId(Long jobId);
}
