package org.zerock.ziczone.repository.tech;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.tech.TechStack;

import java.util.Collection;
import java.util.List;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    List<TechStack> findByPersonalUserPersonalId(Long personalId);

    List<TechStack> findByPersonalUserPersonalIdIn(List<Long> sellerIds);

    List<TechStack> findByPersonalUser(PersonalUser personalUser);
}
