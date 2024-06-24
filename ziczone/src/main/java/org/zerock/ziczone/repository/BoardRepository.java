package org.zerock.ziczone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
