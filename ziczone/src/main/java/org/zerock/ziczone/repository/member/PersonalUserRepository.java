package org.zerock.ziczone.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.member.PersonalUser;

public interface PersonalUserRepository extends JpaRepository<PersonalUser, Long> {
    PersonalUser findByPersonalId(Long personalId);

}
