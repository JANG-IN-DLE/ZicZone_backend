package org.zerock.ziczone.controller;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.zerock.ziczone.dto.help.BoardDTO;
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
                    .userId(5L) // 임의로 회원번호 5번을 작성자로 설정
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
}