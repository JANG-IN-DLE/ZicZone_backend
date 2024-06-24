package org.zerock.ziczone.domain;

import lombok.*;

import javax.persistence.*;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;
}
