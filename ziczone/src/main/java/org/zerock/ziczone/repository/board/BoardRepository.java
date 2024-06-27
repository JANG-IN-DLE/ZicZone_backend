package org.zerock.ziczone.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.board.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
