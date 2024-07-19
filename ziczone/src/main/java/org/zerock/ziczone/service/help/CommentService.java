package org.zerock.ziczone.service.help;

import org.zerock.ziczone.domain.board.Comment;
import org.zerock.ziczone.dto.help.CommentDTO;

import java.util.List;

public interface CommentService {
    // 댓글 등록
    CommentDTO commentRegister(CommentDTO commentDTO);
    // 댓글 조회
    List<CommentDTO> boardReadAllComment(Long corrId);
    // 댓글 수정
    CommentDTO commentModify(CommentDTO commentDTO);
    // 댓글 삭제
    void commentDelete(Long userId, Long commId);
    // 정보 추가(댓글 수정에서 필요)
    CommentDTO commentUserRead(Comment comment);
    // 댓글 채택
    void selectComment(Long commId, Long userId);
    // 회원별 댓글 조회
    List<CommentDTO> userReadAllComment(Long userId);
    // 게시물 삭제 시 전체 댓글 삭제
    void deleteCommentsByCorrId(Long corrId);
}