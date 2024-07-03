package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.PickAndScrap;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.PersonalUser;

import java.util.List;

public interface PickAndScrapRepository extends JpaRepository<PickAndScrap, Long> {
    // 주어진 PersonalUser와 관련된 모든 PickAndScrap 항목 중 pick이 true인 항목을 찾음
    List<PickAndScrap> findByPersonalUserAndPickTrue(PersonalUser personalUser);
    List<PickAndScrap> findByCompanyUserAndPickTrue(CompanyUser companyUser);
}
