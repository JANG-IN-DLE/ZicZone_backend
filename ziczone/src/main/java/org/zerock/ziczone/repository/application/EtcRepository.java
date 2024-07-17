package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Etc;
import org.zerock.ziczone.domain.application.Resume;

import java.util.Collection;
import java.util.List;

public interface EtcRepository extends JpaRepository<Etc, Long> {
    List<Etc> findByResume_ResumeId(Long resumeId);


    List<Etc> findByResume(Resume resume);

    void deleteByResumeResumeId(Long resumeId);
}
