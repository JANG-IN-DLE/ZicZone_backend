package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
