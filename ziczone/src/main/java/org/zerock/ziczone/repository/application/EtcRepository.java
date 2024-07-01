package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Etc;

import java.util.List;

public interface EtcRepository extends JpaRepository<Etc, Long> {
    List<Etc> findByResume_ResumeId(Long resumeId);
}
