package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Career;
import org.zerock.ziczone.domain.application.Resume;

import java.util.Collection;
import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findByResume_ResumeId(Long resumeId);


    List<Career>  findByResume(Resume resume);

    void deleteByResumeResumeId(Long resumeId);
}
