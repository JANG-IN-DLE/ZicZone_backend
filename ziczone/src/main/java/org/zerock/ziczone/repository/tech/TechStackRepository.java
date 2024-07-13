package org.zerock.ziczone.repository.tech;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.tech.TechStack;

import java.util.Collection;
import java.util.List;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    List<TechStack> findByPersonalUserPersonalId(Long personalId);

    List<TechStack> findByPersonalUser(PersonalUser personalUser);

    /*
    이렇게 리포지토리를 정의한 후에는 서비스 계층과 컨트롤러 계층에서 이 리포지토리를 사용하여
     personalId로 JobPosition과 TechStack 데이터를 가져올 수 있습니다.
     */
    List<TechStack> findByPersonalUser_PersonalId(Long personalId);

    void deleteByPersonalUser(PersonalUser personalUser);
}
