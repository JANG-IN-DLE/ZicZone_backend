package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
