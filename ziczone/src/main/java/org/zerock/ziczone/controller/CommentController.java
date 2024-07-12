package org.zerock.ziczone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.ziczone.dto.help.CommentDTO;
import org.zerock.ziczone.service.help.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        if (commentDTO.getUserId() == null || commentDTO.getCorrId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Long createdCommentId = commentService.commentRegister(commentDTO);
        CommentDTO createdComment = commentService.commentUserRead(commentDTO);

        return ResponseEntity.ok(createdComment);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments(@RequestParam Long corrId) {
        List<CommentDTO> comments = commentService.boardReadAllComment(corrId);

        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}/{userId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @PathVariable Long userId, @RequestBody CommentDTO commentDTO) {
        commentDTO.setCommId(commentId);
        commentDTO.setUserId(userId);

        if (commentDTO.getUserId() == null || commentDTO.getCorrId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        CommentDTO updatedComment = commentService.commentModify(commentDTO);

        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}/{userId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        commentService.commentDelete(userId, commentId);

        return ResponseEntity.ok().build();
    }
}