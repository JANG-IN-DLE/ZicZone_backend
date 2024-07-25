package org.zerock.ziczone.repository.board;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.ziczone.domain.board.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 회원별 조회
    List<Board> findByUserUserId(Long userId);
    // 최신순 조회
    Page<Board> findAllByOrderByCorrCreateDesc(Pageable pageable);
    // 조회순 조회
    Page<Board> findAllByOrderByCorrViewDesc(Pageable pageable);
    // 포인트(베리)순 조회
    Page<Board> findAllByOrderByCorrPointDesc(Pageable pageable);
    // 채택되지 않은 게시물을 최신순으로 조회
    @Query("SELECT b FROM Board b " +
            "WHERE (SELECT COUNT(c) FROM Comment c WHERE c.board = b AND c.commSelection = true) = 0 " +
            "ORDER BY b.corrCreate DESC")
    Page<Board> findAllByOrderByCorrCreateDescAndCommSelectionFalse(Pageable pageable);
    // 채택되지 않은 게시물을 조회순으로
    @Query("SELECT b FROM Board b " +
            "WHERE (SELECT COUNT(c) FROM Comment c WHERE c.board = b AND c.commSelection = true) = 0 " +
            "ORDER BY b.corrView DESC")
    Page<Board> findAllByOrderByCorrViewDescAndCommSelectionFalse(Pageable pageable);
    // 채택되지 않은 게시물을 포인트(베리)순으로 조회
    @Query("SELECT b FROM Board b " +
            "WHERE (SELECT COUNT(c) FROM Comment c WHERE c.board = b AND c.commSelection = true) = 0 " +
            "ORDER BY b.corrPoint DESC")
    Page<Board> findAllByOrderByCorrPointDescAndCommSelectionFalse(Pageable pageable);
    // 조회수 증가
    @Modifying
    @Query("UPDATE Board b SET b.corrView = b.corrView + 1 WHERE b.corrId = :corrId")
    void boardViewCount(@Param("corrId") Long corrId);
    // 7일 지난 댓글 없는 게시물 삭제
    @Modifying
    @Query("DELETE FROM Board b WHERE b.corrCreate < :sevenDaysAgo AND NOT EXISTS (SELECT c FROM Comment c WHERE c.board = b)")
    int deleteOldBoardsWithoutComments(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
    // [알림] corrId로 board 정보 가져오기
    Board findByCorrId(Long corrId);
}
