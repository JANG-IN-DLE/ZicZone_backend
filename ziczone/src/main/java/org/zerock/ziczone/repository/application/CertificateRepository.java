package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Certificate;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByResume_ResumeId(Long ResumeId);

    void deleteByResume_ResumeId(Long resumeId);


    void deleteByResumeResumeId(Long resumeId);
}
