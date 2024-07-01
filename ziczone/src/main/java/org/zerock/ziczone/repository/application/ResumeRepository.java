package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Resume findByResumeId(Long resumeId);
    Resume findByPersonalUser_PersonalId(Long personalId);
}
