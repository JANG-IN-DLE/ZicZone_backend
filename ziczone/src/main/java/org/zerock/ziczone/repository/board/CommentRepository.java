package org.zerock.ziczone.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.board.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
