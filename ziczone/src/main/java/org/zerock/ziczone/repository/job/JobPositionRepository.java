package org.zerock.ziczone.repository.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.job.JobPosition;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.dto.mypage.JobPositionDTO;

import java.util.Collection;
import java.util.List;

public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {
    List<JobPosition> findByPersonalUserPersonalId(Long personalId);

    List<JobPosition> findByPersonalUser(PersonalUser personalUser);

    /*
     Spring Data JPA는 연관된 엔티티의 속성을 탐색할 때 중간에 언더스코어(_)가 필요합니다.
     여기서 PersonalUser는 JobPosition 또는 TechStack 엔티티와 연관된 엔티티입니다.
     *findByPersonalUser_PersonalId**는 JobPosition 또는 TechStack 엔티티에서 PersonalUser 엔티티의 personalId 속성을 기준으로 데이터를 조회합니다.
     즉, JobPosition이나 TechStack 엔티티가 PersonalUser 엔티티와 연관되어 있고, 이 PersonalUser 엔티티의 personalId를 이용하여 데이터를 가져옵니다.
     */
    List<JobPosition> findByPersonalUser_PersonalId(Long personalId);

    void deleteByPersonalUser(PersonalUser personalUser);

    void deleteByPersonalUserPersonalId(Long personalUserId);
}
