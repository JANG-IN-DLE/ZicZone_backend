package org.zerock.ziczone.dto.help;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private Long corrId; // 게시물 ID

    private Integer corrPoint; // 게시물에서 선택할 베리

    private String corrTitle; // 게시물 제목

    private String corrContent; // 게시물 내용

    private String corrPdf; // 게시물 pdf

    private LocalDateTime corrModify; // 게시물 수정시간

    @Builder.Default
    private Integer corrView = 0; // 게시물 조회수

    private Long userId; // 게시물 작성자 ID

    private Boolean commSelection; // 채택 여부

    private String userName; // 게시물 작성자 이름

    private String personalCareer; // 게시물 작성자 경력

    private List<CommentDTO> commentList; // 댓글 목록
}
