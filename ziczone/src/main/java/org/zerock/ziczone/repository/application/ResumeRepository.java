package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Resume;
import org.zerock.ziczone.domain.member.PersonalUser;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Resume findByResumeId(Long resumeId);
    Resume findByPersonalUser_PersonalId(Long personalId);
    Optional<List<Resume>> findByPersonalUser(PersonalUser personalUser);

    List<Resume> findByPersonalUserPersonalIdIn(List<Long> sellerIds);

    List<Resume> findByPersonalUserPersonalId(Long personalId);
}
