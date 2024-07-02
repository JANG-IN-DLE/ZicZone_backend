package org.zerock.ziczone.service.help;

import org.zerock.ziczone.dto.help.BoardDTO;
import org.zerock.ziczone.dto.page.PageRequestDTO;
import org.zerock.ziczone.dto.page.PageResponseDTO;

import java.util.List;

public interface BoardService {
    // 게시물 등록
    Long boardRegister(BoardDTO boardDTO);
    // 게시물 ID별 조회
    BoardDTO boardReadOne(Long corrId);
    // 회원별 게시물 조회
    List<BoardDTO> userReadAll(Long userId);
    // 게시물 전체 조회 (필터링 및 페이지네이션)
    PageResponseDTO<BoardDTO> boardFilter(String filterType, PageRequestDTO pageRequestDTO);
    // 게시물 수정
    void boardModify(BoardDTO boardDTO);
    // 게시물 삭제
    void boardDelete(Long userId, Long corrId);
}
