package org.zerock.ziczone.domain.member;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personalId;

    @Column(length = 100, nullable = false)
    private String personalCareer;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean isPersonalVisible;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean isCompanyVisible;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
