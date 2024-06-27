package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Career;

public interface CareerRepository extends JpaRepository<Career, Long> {
}
