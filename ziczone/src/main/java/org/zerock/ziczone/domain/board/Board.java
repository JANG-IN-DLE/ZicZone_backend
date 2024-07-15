package org.zerock.ziczone.domain.board;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.zerock.ziczone.domain.member.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long corrId;            // id

    @Column(length = 100, nullable = false)
    private String corrTitle;       // 게시물 제목

    @Column(length = 500, nullable = false)
    private String corrContent;     // 게시물 내용

    @Column(length = 2048, nullable = false)
    private String corrPdf;         // 게시물 파일

//    @Column(length = 2048, nullable = false)
//    private String corrPdfUrl;         // 게시물 파일 URL

    @Column(nullable = false)
    private Integer corrPoint;      // 게시물 등록 포인트

    @Builder.Default
    @Column(nullable = false)
    private Integer corrView = 0;   // 게시물 조회수

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime corrCreate;   // 게시물 생성 날짜

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime corrModify;   // 게시물 업데이트 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                  // 유저 테이블

    // 게시물 수정할 때 사용하는 메소드(제목, 내용, pdf 파일 변경 가능)
    public void change(String corrTitle, String corrContent, String corrPdf) {
        this.corrTitle = corrTitle;
        this.corrContent = corrContent;
        this.corrPdf = corrPdf;
    }

    // 조회수 증가 메소드
    public void boardViewCount() {
        this.corrView += 1;
    }
}
