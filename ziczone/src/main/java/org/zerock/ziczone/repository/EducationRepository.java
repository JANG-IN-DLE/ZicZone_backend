package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Education;

public interface EducationRepository extends JpaRepository<Education, Long> {
}
