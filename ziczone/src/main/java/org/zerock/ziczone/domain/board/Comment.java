package org.zerock.ziczone.domain.board;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.zerock.ziczone.domain.member.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commId;                // id
    
    private String commContent;         // 댓글 내용

    private boolean commSelection;      // 채택 여부

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime commCreate;   // 댓글 등록 날짜

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime commModify;   // 댓글 수정 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corr_id")
    private Board board;                // 게시판 테이블

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                  // 유저 테이블

    public void change(String commContent) {
        this.commContent = commContent;
    }

    public void changeSelection(boolean commSelection) {
        this.commSelection = commSelection;
    }
}
