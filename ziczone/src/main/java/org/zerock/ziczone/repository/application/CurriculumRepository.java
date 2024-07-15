package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Curriculum;
import org.zerock.ziczone.domain.application.Resume;

import java.util.Collection;
import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByResume_ResumeId(Long resumeId);

    void deleteByResume_ResumeId(Long resumeId);

    void deleteByResumePersonalUserPersonalId(Long personalUserId);

    List<Curriculum> findByResume(Resume resume);

    void deleteByResumeResumeId(Long resumeId);
}
