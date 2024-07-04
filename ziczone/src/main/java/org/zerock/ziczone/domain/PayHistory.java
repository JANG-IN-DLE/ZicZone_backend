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
    private Long payHistoryId;

    // 팔리는 사람
    private Long sellerId;

    // 사는 사람
    @Column(nullable = false)
    private Long buyerId;

    // 포인트 (ex. -50)
    @Column(length = 100, nullable = false)
    private String berryBucket;

    // 내용 (ex. "이력서 조회")
    @Column(nullable = false)
    private String payHistoryContent;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime payHistoryDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;
}
