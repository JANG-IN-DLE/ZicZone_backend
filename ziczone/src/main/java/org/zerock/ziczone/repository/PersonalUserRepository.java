package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.PersonalUser;

public interface PersonalUserRepository extends JpaRepository<PersonalUser, Long> {
}
