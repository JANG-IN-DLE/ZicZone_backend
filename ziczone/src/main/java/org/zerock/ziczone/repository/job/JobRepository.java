package org.zerock.ziczone.repository.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.job.Job;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Job findByJobId(Long jobId);
}
