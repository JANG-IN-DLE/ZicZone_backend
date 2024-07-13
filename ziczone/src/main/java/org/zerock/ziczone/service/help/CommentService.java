package org.zerock.ziczone.service.help;

import org.zerock.ziczone.domain.board.Comment;
import org.zerock.ziczone.dto.help.CommentDTO;

import java.util.List;

public interface CommentService {
    // 댓글 등록
    CommentDTO commentRegister(CommentDTO commentDTO);
    // 회원별 댓글 조회
    List<CommentDTO> userReadAllComment(Long userId);
    // 게시물 ID별 댓글 조회
    List<CommentDTO> boardReadAllComment(Long corrId);
    // 댓글 수정
    CommentDTO commentModify(CommentDTO commentDTO);
    // 댓글 삭제
    void commentDelete(Long userId, Long commId);
    // 정보 추가(userName, personalCareer)된 CommentDTO
    CommentDTO commentUserRead(Comment comment);
}