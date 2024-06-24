package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.CompanyUser;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {
    CompanyUser findByCompanyId(Long companyId);
}
