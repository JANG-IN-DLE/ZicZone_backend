package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Archive;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {
}
