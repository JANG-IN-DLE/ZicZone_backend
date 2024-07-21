package org.zerock.ziczone.service.help;

import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.domain.board.Board;
import org.zerock.ziczone.dto.help.BoardDTO;
import org.zerock.ziczone.dto.help.BoardProfileCardDTO;
import org.zerock.ziczone.dto.page.PageRequestDTO;
import org.zerock.ziczone.dto.page.PageResponseDTO;

import java.util.List;

public interface BoardService {
    // 게시물 등록
    Long boardRegister(int corrPoint, String corrTitle, String corrContent, MultipartFile corrPdf, Long userId);
    // 게시물 작성자 프로필 카드 조회 (게시물 등록할 때)
    BoardProfileCardDTO registerUserProfile(Long userId);
    // 게시물 조회
    BoardDTO boardReadOne(Long corrId);
    // 게시물 작성자 프로필 카드 조회 (게시물 조회할 때)
    BoardProfileCardDTO boardUserProfile(Long corrId);
    // 게시물 전체 조회 (필터링 및 페이지네이션)
    PageResponseDTO<BoardDTO> boardFilter(String filterType, PageRequestDTO pageRequestDTO, boolean showSelect);
    // 게시물 수정
    Long boardModify(Long corrId, Long userId, String corrTitle, String corrContent, MultipartFile corrPdf, String existingFileName);
    // 게시물 삭제
    void boardDelete(Long userId, Long corrId);
    // 정보 추가(게시물 수정에서 필요)
    BoardDTO boardUserRead(Board board);
    // 조회수 (userId가 같으면 조회수 증가안함)
    void boardViewCount(Long userId, Long corrId);
    // 회원별 게시물 조회
    List<BoardDTO> userReadAll(Long userId);
}
