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
public class AppPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appPaymentId;

    @Column(nullable = false)
    private Long sellUserId;

    @Column(nullable = false)
    private Long berryBucket;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime appPaymentDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;
}
