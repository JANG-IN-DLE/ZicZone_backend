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
    private Long payId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayState payState;

    @Column(nullable = false)
    private Long payNum;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime payDate;

    @Column(nullable = false)
    private Long berryPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;
    
    // berry_point를 차감하는 메서드
    public void subtractBerryPoints(Long points) {
        if(this.berryPoint < points) {
            throw new IllegalArgumentException("Not enough berry points");
        }
        this.berryPoint -= points;
    }
}
