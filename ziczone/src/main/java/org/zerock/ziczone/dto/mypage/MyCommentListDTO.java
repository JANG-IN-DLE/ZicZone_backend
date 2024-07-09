package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MyCommentListDTO {
    private Long commId; // 댓글 ID

    private String commContent; // 댓글 내용

    private boolean commSelection; // 댓글 채택 여부

    private Long userId; // 댓글 작성자 ID

    private String userName; // 댓글 작성자 이름

    private String personalCareer; // 댓글 작성자 경력

    private Long corrId; // 게시물 ID
    
    private Integer corrPoint; // 게시물 등록 포인트
}
