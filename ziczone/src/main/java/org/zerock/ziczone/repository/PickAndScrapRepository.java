package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.PickAndScrap;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.PersonalUser;

import java.util.List;
import java.util.Optional;

public interface PickAndScrapRepository extends JpaRepository<PickAndScrap, Long> {
    // 주어진 PersonalUser와 관련된 모든 PickAndScrap 항목 중 pick이 true인 항목을 찾음
    List<PickAndScrap> findByPersonalUserAndPickTrue(PersonalUser personalUser);
    List<PickAndScrap> findByCompanyUserAndPickTrue(CompanyUser companyUser);
    List<PickAndScrap> findByCompanyUserAndScrapTrue(CompanyUser companyUser);



    // pickAndScrap에서 companyUser와 personalUser를 찾는다.
    Optional<PickAndScrap> findByCompanyUserAndPersonalUser(CompanyUser companyUser, PersonalUser personalUser);
    // pickAndScrap에서 personalUser를 찾는다.한 기업이 여러명 scrap할 수 있으니까 List
    List<PickAndScrap> findByPersonalUser(PersonalUser personalUser);
    // 로그인한 사람companyUser가 scrap한 사람들을 찾는다.
    List<PickAndScrap> findByCompanyUser(CompanyUser companyUser);


}
