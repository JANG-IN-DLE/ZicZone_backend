package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Archive;
import org.zerock.ziczone.domain.application.Resume;
import org.zerock.ziczone.dto.mypage.ArchiveDTO;

import java.util.List;
import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {
    List<Archive> findByResume_ResumeId(Long resumeId);

    Optional<Archive> findByResume(Resume resume);

    void deleteByResumeResumeId(Long resumeId);
}
