package org.zerock.ziczone.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.PersonalUser;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private CompanyUser companyUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;

    // scrap을 true false 전환하는 메서드
    public void toggleScrap(){
        this.scrap = !this.scrap;
    }
    // pick을 true false 전환하는 메서드
    public void togglePick(){
        this.pick = !this.pick;
    }
}
