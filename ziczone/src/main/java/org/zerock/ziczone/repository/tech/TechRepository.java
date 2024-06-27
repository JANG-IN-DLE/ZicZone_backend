package org.zerock.ziczone.repository.tech;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.tech.Tech;

public interface TechRepository extends JpaRepository<Tech, Long> {
    Tech findByTechId(Long techId);
}
