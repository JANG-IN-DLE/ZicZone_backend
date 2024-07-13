//package org.zerock.ziczone.service;
//
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.zerock.ziczone.dto.help.BoardDTO;
//import org.zerock.ziczone.dto.help.BoardProfileCardDTO;
//import org.zerock.ziczone.dto.page.PageRequestDTO;
//import org.zerock.ziczone.dto.page.PageResponseDTO;
//import org.zerock.ziczone.service.help.BoardService;
//
//import java.util.List;
//
//@SpringBootTest
//@Log4j2
//public class BoardServiceTests {
//    @Autowired
//    private BoardService boardService;
//
//    // 개인 회원
//    private final Long userId = 5L;
//
////    기업 회원 ("기업 회원은 게시물을 등록할 수 없습니다." 게시물 생성 안됨)
////    private final Long userId = 4L;
//
//    @Test
//    public void testBoardRegister() {
//        log.info(boardService.getClass().getName());
//
//        BoardDTO boardDTO = BoardDTO.builder()
//                .corrPoint(1000)
//                .corrTitle("게시판 서비스 제목 테스트")
//                .corrContent("게시판 서비스 내용 테스트")
//                .corrPdf("게시판_서비스.pdf")
//                .userId(userId)
//                .build();
//
//        Long corrId = boardService.boardRegister(boardDTO);
//    }
//
//    @Test
//    public void testBoardReadOne() {
//        Long corrId = 37L;
//
//        BoardDTO boardDTO = boardService.boardReadOne(corrId);
//
//        log.info("BoardDTO : " + boardDTO);
//    }
//
//    @Test
//    public void testUserReadAll() {
//        Long userId = 5L;
//
//        List<BoardDTO> boardDTOList = boardService.userReadAll(userId);
//
//        log.info("List<BoardDTO> : " + boardDTOList);
//    }
//
//    @Test
//    public void testBoardFilterLatest() {
//        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 8);
//
//        PageResponseDTO<BoardDTO> result = boardService.boardFilter("latest", pageRequestDTO);
//
//        log.info("latest : " + result);
//    }
//
//    @Test
//    public void testBoardFilterViews() {
//        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 8);
//
//        PageResponseDTO<BoardDTO> result = boardService.boardFilter("views", pageRequestDTO);
//
//        log.info("views : " + result);
//    }
//
//    @Test
//    public void testBoardFilterBerry() {
//        PageRequestDTO pageRequestDTO = new PageRequestDTO(1, 8);
//
//        PageResponseDTO<BoardDTO> result = boardService.boardFilter("berry", pageRequestDTO);
//
//        log.info("berry : " + result);
//    }
//
//    @Test
//    public void testBoardModify() {
//        BoardDTO boardDTO = BoardDTO.builder()
//                .corrId(13L)
//                .corrTitle("변경된 제목 테스트")
//                .corrContent("변경된 내용 테스트")
//                .corrPdf("변경된.pdf")
//                .build();
//
//        boardService.boardModify(boardDTO);
//    }
//
//    @Test
//    public void testBoardDelete() {
//        boardService.boardDelete(5L, 8L);
//
////        "작성자만 삭제할 수 있습니다."(게시물 삭제 안됨)
////        boardService.boardDelete(1L, 10L);
//    }
//
//    // 작성자 프로필 카드 조회
//    @Test
//    public void testBoardProfileCard() {
//        Long corrId = 37L;
//
//        BoardProfileCardDTO boardProfileCard = boardService.boardUserProfile(corrId);
//
//        log.info("BoardProfileCardDTO: " + boardProfileCard);
//    }
//}
