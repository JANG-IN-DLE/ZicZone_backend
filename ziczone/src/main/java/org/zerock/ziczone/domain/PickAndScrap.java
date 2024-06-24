package org.zerock.ziczone.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PickAndScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pickId;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean pick;

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean scrap;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private CompanyUser companyUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;


}
