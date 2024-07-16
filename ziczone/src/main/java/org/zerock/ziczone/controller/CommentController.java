package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.dto.help.CommentDTO;
import org.zerock.ziczone.service.help.CommentService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Log4j2
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 등록
     *
     * @param commentDTO 등록할 댓글 정보
     * @return ResponseEntity<CommentDTO> 생성된 댓글 정보
     * @throws IllegalArgumentException userId나 corrId가 null인 경우 발생
     */
    @PostMapping("/api/personal/comments")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        if (commentDTO.getUserId() == null || commentDTO.getCorrId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        CommentDTO createdComment = commentService.commentRegister(commentDTO);

        return ResponseEntity.ok(createdComment);
    }

    /**
     * 특정 게시물의 모든 댓글 조회
     *
     * @param corrId 조회할 게시물 ID
     * @return ResponseEntity<List<CommentDTO>> 댓글 목록
     * @throws IllegalArgumentException corrId가 null인 경우 발생
     */
    @GetMapping(("/api/user/comments/{corrId}"))
    public ResponseEntity<List<CommentDTO>> getAllComments(@PathVariable Long corrId) {
        if (corrId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<CommentDTO> comments = commentService.boardReadAllComment(corrId);

        return ResponseEntity.ok(comments);
    }

    /**
     * 댓글 수정
     *
     * @param commId    수정할 댓글 ID
     * @param userId    수정하는 사용자 ID
     * @param commentDTO 수정할 댓글 정보
     * @return ResponseEntity<CommentDTO> 수정된 댓글 정보
     * @throws IllegalArgumentException userId나 corrId가 null인 경우 발생
     */
    @PutMapping("/api/personal/comments/{commId}/{userId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commId, @PathVariable Long userId, @RequestBody CommentDTO commentDTO) {
        commentDTO.setCommId(commId);
        commentDTO.setUserId(userId);

        if (commentDTO.getUserId() == null || commentDTO.getCorrId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        CommentDTO updatedComment = commentService.commentModify(commentDTO);

        return ResponseEntity.ok(updatedComment);
    }

    /**
     * 댓글 삭제
     *
     * @param commId 삭제할 댓글 ID
     * @param userId 삭제를 수행하는 사용자 ID
     * @return ResponseEntity<Void> 응답 상태
     */
    @DeleteMapping("/api/personal/comments/{commId}/{userId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commId, @PathVariable Long userId) {
        try {
            commentService.commentDelete(userId, commId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 댓글 채택
     *
     * @param commId 채택할 댓글 ID
     * @param userId 채택을 수행하는 사용자 ID
     * @return ResponseEntity<String> 응답 메시지
     * @throws IllegalArgumentException 댓글 ID가 없거나, 게시물 작성자가 아니거나, 자신의 댓글을 채택하려고 하거나, 이미 채택된 댓글이 있는 경우 발생
     */
    @PostMapping("/api/personal/comments/{commId}/select")
    public ResponseEntity<String> selectComment(@PathVariable Long commId, @RequestParam Long userId) {
        try {
            commentService.selectComment(commId, userId);
            return ResponseEntity.ok("댓글이 채택되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}