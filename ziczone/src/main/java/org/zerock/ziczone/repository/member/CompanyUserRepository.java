package org.zerock.ziczone.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.ziczone.domain.member.CompanyUser;

import java.util.List;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {
    CompanyUser findByCompanyId(Long companyId);

    CompanyUser findByUser_UserId(Long userId);

    @Query("SELECT c.companyLogoUrl FROM CompanyUser c")
    List<String> findAllCompanyLogo();

}
