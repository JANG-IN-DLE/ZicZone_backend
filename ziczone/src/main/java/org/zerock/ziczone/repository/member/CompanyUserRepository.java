package org.zerock.ziczone.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.member.CompanyUser;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {
    CompanyUser findByCompanyId(Long companyId);

    CompanyUser findByUser_UserId(Long userId);

}
