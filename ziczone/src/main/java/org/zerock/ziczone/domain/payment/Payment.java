package org.zerock.ziczone.domain.payment;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.zerock.ziczone.domain.member.PersonalUser;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder(toBuilder = true)
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
    private Integer amount;            // 결제한 금액

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime payDate;      // 결제 날짜

    @Column(nullable = false)
    private String paymentKey;
    /*
    결제의 키 값입니다. 최대 길이는 200자입니다. 결제를 식별하는 역할로, 중복되지 않는 고유한 값입니다. 결제 데이터 관리를 위해 반드시 저장해야 합니다. 결제 상태가 변해도 값이 유지됩니다.
     */

    @Column(nullable = false)
    private Integer berryPoint;         // 저장포인트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;  // 개인 회원 테이블

    @Column(nullable = false)
    private String orderId;
    /*
    주문번호입니다. 최소 길이는 6자, 최대 길이는 64자입니다. 주문한 결제를 식별하는 역할로, 결제를 요청할 때 가맹점에서 만들어서 사용합니다. 결제 상태가 변해도 값이 유지됩니다.
     */



    // berry_point를 차감하는 메서드
    public void subtractBerryPoints(Integer points) {
        if(this.berryPoint < points) {
            throw new IllegalArgumentException("Not enough berry points");
        }
        this.berryPoint -= points;
    }

    // TODO: Payment가 아니라 PaymentHistory(수정필요)
    // 게시물 작성 시 베리 포인트 차감
    public void deductionBoardBerryPoint (int corrPoint) {
        if (this.berryPoint >= corrPoint) {
            this.berryPoint -= corrPoint;
        } else {
            throw new IllegalArgumentException("보유한 베리 포인트가 부족합니다.");
        }
    }

    // 만약, 댓글 채택된 작성자가 payment 값이 없을 경우
    public void initializePayment(PersonalUser personalUser, String orderId, String paymentKey) {
        this.personalUser = personalUser;
        this.berryPoint = 0;
        this.amount = 0;
        this.orderId = orderId;
        this.payDate = LocalDateTime.now();
        this.payState = PayState.ADOPT;
        this.paymentKey = paymentKey;
    }
}
