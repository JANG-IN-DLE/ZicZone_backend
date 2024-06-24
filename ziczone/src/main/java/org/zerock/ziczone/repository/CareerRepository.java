package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Career;

public interface CareerRepository extends JpaRepository<Career, Long> {
}
