package org.zerock.ziczone.repository.board;

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
    // 조회수 증가
    @Modifying
    @Query("UPDATE Board b SET b.corrView = b.corrView + 1 WHERE b.corrId = :corrId")
    void boardViewCount(@Param("corrId") Long corrId);
}
