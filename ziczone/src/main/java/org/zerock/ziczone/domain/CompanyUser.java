package org.zerock.ziczone.domain;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompanyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    @Column(length = 100, nullable = false)
    private String companyNum;

    @Column(length = 100, nullable = false)
    private String companyAddr;

    @Column(nullable = false)
    private LocalDate companyYear;

    @Column(nullable = false)
    private String companyLogo;

    @Column(nullable = false)
    private String companyCeo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

}
