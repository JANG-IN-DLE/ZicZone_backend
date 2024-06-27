package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Education;

public interface EducationRepository extends JpaRepository<Education, Long> {
}
