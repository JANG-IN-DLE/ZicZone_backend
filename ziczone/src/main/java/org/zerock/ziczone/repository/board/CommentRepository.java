package org.zerock.ziczone.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ziczone.domain.board.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 회원별 조회
    List<Comment> findByUserUserId(Long userId);
    // 게시물별 조회
    List<Comment> findByBoardCorrId(Long corrId);
    // 채택된 게시물 여부
    boolean existsByBoardCorrIdAndCommSelection(Long corrId, boolean commSelection);
    // 게시물 삭제 시 전체 댓글 삭제
    void deleteByBoardCorrId(Long corrId);
}
