package org.zerock.ziczone.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.dto.help.BoardDTO;
import org.zerock.ziczone.dto.help.BoardProfileCardDTO;
import org.zerock.ziczone.dto.page.PageRequestDTO;
import org.zerock.ziczone.dto.page.PageResponseDTO;
import org.zerock.ziczone.service.help.BoardService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {

    private final AmazonS3 amazonS3;
    private final BoardService boardService;

    /**
     * 첨삭 게시물 등록
     * (특히, 파일은 S3에 업로드하고, 업로드된 파일의 URL을 데이터베이스에 저장)
     *
     * @param corrPoint  게시물 포인트
     * @param corrTitle  게시물 제목
     * @param corrContent 게시물 내용
     * @param corrPdf    첨부 파일 (MultipartFile 형태)
     * @return ResponseEntity<String>  응답 메시지
     */
    @PostMapping("/post")
    public ResponseEntity<Map<String, String>> createBoard(@RequestParam("berry") int corrPoint,
                                                           @RequestParam("title") String corrTitle,
                                                           @RequestParam("content") String corrContent,
                                                           @RequestParam("file") MultipartFile corrPdf) {
        String bucketName = "ziczone-bucket";
        String folderName = "CorrPdf/";

        String objectName = folderName + corrPdf.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(corrPdf.getSize());
            amazonS3.putObject(new PutObjectRequest(bucketName, objectName, corrPdf.getInputStream(), metadata));
            String fileUrl = amazonS3.getUrl(bucketName, objectName).toString();

            BoardDTO boardDTO = BoardDTO.builder()
                    .corrPoint(corrPoint)
                    .corrTitle(corrTitle)
                    .corrContent(corrContent)
                    .corrPdf(fileUrl)
                    .userId(13L) // 임의로 작성자로 설정
                    .build();

            Long corrId = boardService.boardRegister(boardDTO);
            log.info("Generated corrId: {}", corrId);

            Map<String, String> response = new HashMap<>();
            response.put("corrId", corrId.toString());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "파일 업로드 실패: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "서버 오류: " + e.getMessage()));
        }
    }

    /**
     * 게시물 등록할 때 나의 프로필 카드 조회
     *
     * @param () 유저 ID (로그인 구현되면)
     * @return ResponseEntity<BoardProfileCardDTO> 조회된 프로필 카드 정보
     */
    @GetMapping("/myProfile")
    public ResponseEntity<BoardProfileCardDTO> getUserProfileCard() {
        Long userId = 13L; // 임의로 회원 7 설정

        BoardProfileCardDTO profileCardDTO = boardService.UserProfile(userId);

        return ResponseEntity.ok(profileCardDTO);
    }

    /**
     * 특정 게시물 작성자 프로필 카드 조회
     *
     * @param corrId 게시물 ID
     * @return ResponseEntity<BoardProfileCardDTO> 조회된 프로필 카드 정보
     */
    @GetMapping("/profile/{corrId}")
    public ResponseEntity<BoardProfileCardDTO> getBoardProfileCard(@PathVariable Long corrId) {
        BoardProfileCardDTO profileCardDTO = boardService.boardUserProfile(corrId);

        return ResponseEntity.ok(profileCardDTO);
    }

    /**
     * 특정 게시물 조회
     *
     * @param corrId 게시물 ID
     * @return ResponseEntity<BoardDTO> 조회된 게시물 정보
     */
    @GetMapping("/{corrId}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long corrId) {
        BoardDTO boardDTO = boardService.boardReadOne(corrId);

        return ResponseEntity.ok(boardDTO);
    }

    /**
     * HELP존 리스트 정렬(최신순, 조회순, 베리순)
     *
     * @param filterType 정렬 기준 (latest: 최신순, views: 조회순, berry: 베리순)
     * @param page       요청할 페이지 번호
     * @param size       페이지당 항목 수
     * @return PageResponseDTO<BoardDTO> 페이지 응답 DTO
     */
    @GetMapping("/filter")
    public PageResponseDTO<BoardDTO> boardFilter(
            @RequestParam String filterType,
            @RequestParam int page,
            @RequestParam int size) {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .build();

        return boardService.boardFilter(filterType, pageRequestDTO);
    }

    /**
     * 게시물 수정
     *
     * @param boardDTO 게시물 수정에 필요한 데이터
     * @return ResponseEntity<Void> 상태 코드 응답
     */
    @PutMapping("/modify")
    public ResponseEntity<Void> modifyBoard(@RequestBody BoardDTO boardDTO) {
        boardService.boardModify(boardDTO);

        return ResponseEntity.ok().build();
    }

    /**
     * 게시물 삭제
     *
     * @param userId   사용자 ID
     * @param corrId   게시물 ID
     * @return ResponseEntity<Void> 상태 코드 응답
     */
    @DeleteMapping("/{userId}/{corrId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long userId, @PathVariable Long corrId) {
        boardService.boardDelete(userId, corrId);

        return ResponseEntity.ok().build();
    }

    /**
     * 게시물 조회수 증가
     *
     * @param userId 조회하는 사용자 ID
     * @param corrId 게시물 ID
     * @return ResponseEntity<Void> 상태 코드 200을 반환
     */
    @PutMapping("/viewCnt/{userId}/{corrId}")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long userId, @PathVariable Long corrId) {
        boardService.boardViewCount(userId, corrId);
        return ResponseEntity.ok().build();
    }
}