package org.zerock.ziczone.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.ziczone.domain.member.PersonalUser;

import java.util.List;

public interface PersonalUserRepository extends JpaRepository<PersonalUser, Long> {
    PersonalUser findByPersonalId(Long personalId);

    PersonalUser findByUser_UserId(Long personalId);

    // JPQL 쿼리를 사용하여 isPersonalVisible 속성이 true인 PersonalUser의 personalId 목록을 반환
    @Query("SELECT p.personalId FROM PersonalUser p WHERE p.isPersonalVisible = true")
    List<Long> findPersonalUserIdsByIsPersonalVisibleTrue();

    // JPQL 쿼리를 사용하여 isCompanyVisible 속성이 true인 PersonalUser의 personalId 목록을 반환
    @Query("SELECT p.personalId FROM PersonalUser p WHERE p.isCompanyVisible = true")
    List<Long> findPersonalUserIdsByIsCompanyVisibleTrue();

    List<PersonalUser> findByPersonalIdIn(List<Long> sellerIds);
}
