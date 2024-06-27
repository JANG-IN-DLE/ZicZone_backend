package org.zerock.ziczone.repository.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.application.Archive;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {
}
