package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Certificate;
import org.zerock.ziczone.domain.application.Resume;

import java.util.Collection;
import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByResume_ResumeId(Long ResumeId);


    List<Certificate> findByResume(Resume resume);

    void deleteByResumeResumeId(Long resumeId);
}
