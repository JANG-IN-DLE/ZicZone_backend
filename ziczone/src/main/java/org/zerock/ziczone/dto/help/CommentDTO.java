package org.zerock.ziczone.dto.help;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.Gender;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long commId; // 댓글 ID

    private String commContent; // 댓글 내용

    private boolean commSelection; // 댓글 채택 여부

    private Long userId; // 댓글 작성자 ID

    private Long personalId; // 개인 회원 ID

    private String userName; // 댓글 작성자 이름

    private String personalCareer; // 댓글 작성자 경력

    private Gender gender; // 성별

    private Long corrId; // 게시물 ID

    private Integer corrPoint; // 게시물에서 선택한 베리

    private LocalDateTime commModify; // 댓글 수정 시간

}
