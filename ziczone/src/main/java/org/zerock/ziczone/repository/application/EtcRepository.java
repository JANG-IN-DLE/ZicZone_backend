package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Etc;

public interface EtcRepository extends JpaRepository<Etc, Long> {
}
