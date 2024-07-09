package org.zerock.ziczone.domain.payment;

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
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payId;             // id

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayState payState;      // 결제 상태

    @Column(nullable = false)
    private Integer payNum;            // 결제 금액

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime payDate;      // 결제 날짜

    @Column(nullable = false)
    private Integer berryPoint;         // 저장포인트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;  // 개인 회원 테이블
    
    // berry_point를 차감하는 메서드
    public void subtractBerryPoints(Integer points) {
        if(this.berryPoint < points) {
            throw new IllegalArgumentException("Not enough berry points");
        }
        this.berryPoint -= points;
    }
}
