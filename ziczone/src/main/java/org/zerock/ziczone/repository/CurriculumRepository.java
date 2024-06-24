package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Curriculum;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
}
