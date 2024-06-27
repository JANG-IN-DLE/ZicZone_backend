package org.zerock.ziczone.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.board.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
