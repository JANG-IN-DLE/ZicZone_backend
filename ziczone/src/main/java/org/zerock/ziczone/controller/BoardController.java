package org.zerock.ziczone.controller;

import com.amazonaws.services.s3.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
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

    /**
     * 첨삭 게시물 등록
     * (특히, 파일은 S3에 업로드하고, 업로드된 파일의 URL을 데이터베이스에 저장)
     *
     * @param corrPoint  게시물 포인트
     * @param corrTitle  게시물 제목
     * @param corrContent 게시물 내용
     * @param corrPdf    첨부 파일 (MultipartFile 형태)
     * @param userId     사용자 ID
     * @return ResponseEntity<Map<String, String>> 응답 메시지
     */
    @PostMapping("/api/personal/board/post")
    public ResponseEntity<Map<String, String>> createBoard(@RequestParam("berry") int corrPoint,
                                                           @RequestParam("title") String corrTitle,
                                                           @RequestParam("content") String corrContent,
                                                           @RequestParam("file") MultipartFile corrPdf,
                                                           @RequestParam("userId") Long userId) {
        String bucketName = "ziczone-bucket-jangindle";
        String folderName = "CorrPdf/";
        String objectName = folderName + corrPdf.getOriginalFilename();

        try {
            // S3에 업로드할 파일의 메타데이터 설정하는 객체
            ObjectMetadata metadata = new ObjectMetadata();
            // 파일 크기를 메타데이터에 설정
            metadata.setContentLength(corrPdf.getSize());
            // 파일의 MIME 타입을 "application/pdf"로 설정
            metadata.setContentType("application/pdf");
            // S3에 파일을 업로드
            amazonS3.putObject(new PutObjectRequest(bucketName, objectName, corrPdf.getInputStream(), metadata));
            // 업로드된 파일의 URL 가져오기
            String fileUrl = amazonS3.getUrl(bucketName, objectName).toString();

            // 업로드된 파일의 접근 제어 리스트 가져오기
            AccessControlList accessControlList = amazonS3.getObjectAcl(bucketName, objectName);
            // 모든 사용자에게 읽기 권한 부여
            accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            // 설정한 접근 제어 리스트를 S3 객체에 적용
            amazonS3.setObjectAcl(bucketName, objectName, accessControlList);

            // 서비스 레이어를 호출하여 게시물 등록
            Long corrId = boardService.boardRegister(corrPoint, corrTitle, corrContent, fileUrl, userId);

            // 응답 메시지에 게시물 ID 추가
            Map<String, String> response = new HashMap<>();
            response.put("corrId", corrId.toString());
            response.put("fileName", corrPdf.getOriginalFilename());
            // 응답 메시지 반환
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "파일 업로드 실패: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "서버 오류: " + e.getMessage()));
        }
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
     * 게시물 수정
     *
     * @param corrId    게시물 ID
     * @param userId    사용자 ID
     * @param boardDTO  수정할 게시물 정보
     * @return ResponseEntity<BoardDTO> 수정된 게시물 정보
     */
    @PutMapping("/api/personal/board/{corrId}/{userId}")
    public ResponseEntity<BoardDTO> modifyBoard(@PathVariable Long corrId, @PathVariable Long userId, @RequestBody BoardDTO boardDTO) {
        boardDTO.setCorrId(corrId);
        boardDTO.setUserId(userId);

        BoardDTO updatedBoard = boardService.boardModify(boardDTO);

        return ResponseEntity.ok(updatedBoard);
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