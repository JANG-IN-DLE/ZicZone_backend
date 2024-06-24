package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Tech;

public interface TechRepository extends JpaRepository<Tech, Long> {
    Tech findByTechId(Long techId);
}
