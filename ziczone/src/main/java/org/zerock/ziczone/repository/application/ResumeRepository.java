package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.ziczone.domain.application.Resume;
import org.zerock.ziczone.domain.member.PersonalUser;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Resume findByResumeId(Long resumeId);
    Resume findByPersonalUser_PersonalId(Long personalId);
    // 로그인 안되었을 때 상위 4개만 resumeUpdate를 가지고 최신순으로 데이터 가져옴
    List<Resume> findTop4ByOrderByResumeUpdateDesc();
    // isCompanyvisible이 true인 최신 resume 정보를 포함하는 PersonalUser리스트를 가져옴
    List<Resume> findAllByPersonalUserIsCompanyVisibleTrueOrderByResumeUpdateDesc();
    // isPersonalvisible이 true인 최신 resume 정보를 포함하는 PersonalUser리스트를 가져옴
    List<Resume> findAllByPersonalUserIsPersonalVisibleTrueOrderByResumeUpdateDesc();

    Optional<List<Resume>> findByPersonalUser(PersonalUser personalUser);


    List<Resume> findByPersonalUserPersonalIdIn(List<Long> sellerIds);

    List<Resume> findByPersonalUserPersonalId(Long personalId);
}
