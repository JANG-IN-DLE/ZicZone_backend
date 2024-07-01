package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Education;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByResume_ResumeId(Long resumeId);
}
