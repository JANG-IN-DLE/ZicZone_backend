package org.zerock.ziczone.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.board.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 회원별 조회
    List<Comment> findByUserUserId(Long userId);
    // 게시물별 조회
    List<Comment> findByBoardCorrId(Long boardId);
}
