package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.TechStack;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
}
