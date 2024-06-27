package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Curriculum;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
}
