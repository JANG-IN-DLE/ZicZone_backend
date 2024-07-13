package org.zerock.ziczone.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.ziczone.dto.help.CommentDTO;
import org.zerock.ziczone.repository.member.UserRepository;
import org.zerock.ziczone.service.help.CommentService;

import java.util.List;

@SpringBootTest
@Log4j2
public class CommentServiceTests {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserRepository userRepo;

    // 실제 DB값
    private final Long userId = 7L; // 개인 회원
    private final Long corrId = 2L;

//    @Test
//    public void testCommentRegister() {
//        CommentDTO commentDTO = CommentDTO.builder()
//                .commContent("댓글 테스트")
//                .corrId(corrId)
//                .userId(userId)
//                .build();
//
//        Long commId = commentService.commentRegister(commentDTO);
//    }

    @Test
    public void testUserReadAllComment() {
        Long userId = 13L;

        List<CommentDTO> commentDTOList = commentService.userReadAllComment(userId);

        log.info("List<CommentDTO> : " + commentDTOList);
    }

    @Test
    public void testBoardReadAllComment() {
        Long corrId = 5L;

        List<CommentDTO> commentDTOList = commentService.boardReadAllComment(corrId);

        log.info("List<CommentDTO> : " + commentDTOList);
    }

    @Test
    public void testCommentModify() {
        CommentDTO commentDTO = CommentDTO.builder()
                .commId(1L)
                .userId(userId)
                .corrId(corrId)
                .commContent("변경된 댓글 테스트")
                .build();

        commentService.commentModify(commentDTO);
    }

    @Test
    public void testCommentDelete() {
        commentService.commentDelete(5L, 2L);
    }
}
