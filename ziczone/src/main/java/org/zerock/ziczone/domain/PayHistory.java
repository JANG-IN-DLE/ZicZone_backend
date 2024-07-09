package org.zerock.ziczone.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.zerock.ziczone.domain.member.PersonalUser;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payHistoryId;              // id

    private Long sellerId;                  // 파는 유저테이블 아이디

    @Column(nullable = false)
    private Long buyerId;                   // 사는 유저테이블 아이디

    // 포인트 (ex. -50)
    @Column(length = 100, nullable = false)
    private String berryBucket;             //  지원서 구매 포인트, 채택 포인트, 첨삭 게시물 등록

    // 내용 (ex. "이력서조회")
    @Column(nullable = false)
    private String payHistoryContent;       // 유형별 내용

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime payHistoryDate;   // 결제 날짜

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;      // 개인 회원 테이블
}
