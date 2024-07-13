package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.dto.help.CommentDTO;
import org.zerock.ziczone.service.help.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Log4j2
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        if (commentDTO.getUserId() == null || commentDTO.getCorrId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        CommentDTO createdComment = commentService.commentRegister(commentDTO);

        return ResponseEntity.ok(createdComment);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments(@RequestParam(required = false) Long corrId) {
        if (corrId == null) {
            // corrId가 제공되지 않았을 때의 처리 로직
            return ResponseEntity.badRequest().body(null);
        }
        List<CommentDTO> comments = commentService.boardReadAllComment(corrId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commId}/{userId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commId, @PathVariable Long userId, @RequestBody CommentDTO commentDTO) {
        commentDTO.setCommId(commId);
        commentDTO.setUserId(userId);

        if (commentDTO.getUserId() == null || commentDTO.getCorrId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        CommentDTO updatedComment = commentService.commentModify(commentDTO);

        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commId}/{userId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commId, @PathVariable Long userId) {
        try {
            commentService.commentDelete(userId, commId);
            log.info(commId + " deleted");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}