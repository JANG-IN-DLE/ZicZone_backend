package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Education;
import org.zerock.ziczone.domain.application.Resume;

import java.util.Collection;
import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByResume_ResumeId(Long resumeId);

    void deleteByResume_ResumeId(Long resumeId);

    void deleteByResumePersonalUserPersonalId(Long personalUserId);

    List<Education> findByResume(Resume resume);

    void deleteByResumeResumeId(Long resumeId);
}
