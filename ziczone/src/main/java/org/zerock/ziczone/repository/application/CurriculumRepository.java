package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Curriculum;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByResume_ResumeId(Long resumeId);
}
