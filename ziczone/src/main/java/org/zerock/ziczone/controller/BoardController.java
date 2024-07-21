package org.zerock.ziczone.controller;

import com.amazonaws.services.s3.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.zerock.ziczone.dto.help.BoardDTO;
import org.zerock.ziczone.dto.help.BoardProfileCardDTO;
import org.zerock.ziczone.dto.page.PageRequestDTO;
import org.zerock.ziczone.dto.page.PageResponseDTO;
import org.zerock.ziczone.service.help.BoardService;
import org.zerock.ziczone.service.help.CommentService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class BoardController {
    private final AmazonS3 amazonS3;

    private final BoardService boardService;

    private final CommentService commentService;

    /**
     * 첨삭 게시물 등록
     * (특히, 파일은 S3에 업로드하고, 업로드된 파일의 URL을 데이터베이스에 저장)
     *
     * @param corrPoint  게시물 포인트
     * @param corrTitle  게시물 제목
     * @param corrContent 게시물 내용
     * @param corrPdf    첨부 파일 (MultipartFile 형태)
     * @param userId     사용자 ID
     * @return ResponseEntity<Map<String, Long>> 응답 메시지
     */
    @PostMapping("/api/personal/board/post")
    public ResponseEntity<Map<String, Long>> createBoard(@RequestParam("berry") int corrPoint,
                                                         @RequestParam("title") String corrTitle,
                                                         @RequestParam("content") String corrContent,
                                                         @RequestParam("file") MultipartFile corrPdf,
                                                         @RequestParam("userId") Long userId) {
        Long corrId = boardService.boardRegister(corrPoint, corrTitle, corrContent, corrPdf, userId);

        Map<String, Long> response = new HashMap<>();
        response.put("corrId", corrId);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시물 등록할 때 작성자 프로필 카드 조회
     *
     * @param userId 사용자 ID
     * @return ResponseEntity<BoardProfileCardDTO> 조회된 프로필 카드 정보
     */
    @GetMapping("/api/personal/board/myProfile/{userId}")
    public ResponseEntity<BoardProfileCardDTO> getUserProfileCard(@PathVariable Long userId) {
        BoardProfileCardDTO boardProfileCardDTO = boardService.registerUserProfile(userId);

        return ResponseEntity.ok(boardProfileCardDTO);
    }

    /**
     * 게시물 조회
     *
     * @param corrId 게시물 ID
     * @return ResponseEntity<BoardDTO> 조회된 게시물 정보
     */
    @GetMapping("/api/user/board/{corrId}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long corrId) {
        BoardDTO boardDTO = boardService.boardReadOne(corrId);

        return ResponseEntity.ok(boardDTO);
    }

    /**
     * 게시물 조회할 때 작성자 프로필 카드 조회
     *
     * @param corrId 게시물 ID
     * @return ResponseEntity<BoardProfileCardDTO> 조회된 프로필 카드 정보
     */
    @GetMapping("/api/user/board/profile/{corrId}")
    public ResponseEntity<BoardProfileCardDTO> getBoardProfileCard(@PathVariable Long corrId) {
        BoardProfileCardDTO profileCardDTO = boardService.boardUserProfile(corrId);

        return ResponseEntity.ok(profileCardDTO);
    }

    /**
     * HELP존 리스트 정렬(최신순, 조회순, 베리순)
     *
     * @param filterType 정렬 기준 (latest: 최신순, views: 조회순, berry: 베리순)
     * @param page       요청할 페이지 번호
     * @param size       페이지당 항목 수
     * @param showSelect 채택된 게시물 제외 여부
     * @return PageResponseDTO<BoardDTO> 페이지 응답 DTO
     */
    @GetMapping("/api/user/board/filter")
    public PageResponseDTO<BoardDTO> boardFilter(
            @RequestParam String filterType,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false, defaultValue = "false") boolean showSelect) {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .build();

        return boardService.boardFilter(filterType, pageRequestDTO, showSelect);
    }

    /**
     * 첨삭 게시물 수정
     *
     * @param corrId            게시물 ID
     * @param userId            사용자 ID
     * @param corrTitle         게시물 제목
     * @param corrContent       게시물 내용
     * @param corrPdf           첨부 파일 (MultipartFile 형태, 선택사항)
     * @param existingFileName  기존 파일 이름 (선택사항, 새로운 파일이 업로드되지 않은 경우 사용)
     * @return ResponseEntity<Map<String, Long>> 응답 메시지 (수정된 게시물 ID를 포함)
     */
    @PutMapping(value = "/api/personal/board/{corrId}/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Long>> modifyBoard(@PathVariable Long corrId,
                                                         @PathVariable Long userId,
                                                         @RequestParam("title") String corrTitle,
                                                         @RequestParam("content") String corrContent,
                                                         @RequestPart(value = "file", required = false) MultipartFile corrPdf,
                                                         @RequestParam(value = "existingFileName", required = false) String existingFileName) {
        Long updatedCorrId = boardService.boardModify(corrId, userId, corrTitle, corrContent, corrPdf, existingFileName);

        Map<String, Long> response = new HashMap<>();
        response.put("corrId", updatedCorrId);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시물 삭제
     *
     * @param userId 사용자 ID
     * @param corrId 게시물 ID
     * @return ResponseEntity<Void> 응답 메시지 (삭제 성공 여부)
     */
    @DeleteMapping("/api/personal/board/{corrId}/{userId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long userId, @PathVariable Long corrId) {
        commentService.deleteCommentsByCorrId(corrId);

        boardService.boardDelete(userId, corrId);

        return ResponseEntity.ok().build();
    }

    /**
     * 게시물 조회수 증가
     *
     * @param userId 사용자 ID
     * @param corrId 게시물 ID
     * @return ResponseEntity<Void> 응답 메시지 (조회수 증가 성공 여부)
     */
    @PutMapping("/api/user/board/viewCnt/{userId}/{corrId}")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long userId, @PathVariable Long corrId) {
        boardService.boardViewCount(userId, corrId);

        return ResponseEntity.ok().build();
    }
}