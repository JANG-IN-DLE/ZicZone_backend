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
    private Long pickId;                // id

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean pick;               // pick 선택 유무 : 선택(1), 미선택(0)

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean scrap;              // scrap 선택 유무 : 선택(1), 미선택(0)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private CompanyUser companyUser;    // 기업 회원 테이블

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;  // 개인 회원 테이블

    // scrap을 true false 전환하는 메서드
    public void toggleScrap(){
        this.scrap = !this.scrap;
    }
    // pick을 true false 전환하는 메서드
    public void togglePick(){
        this.pick = !this.pick;
    }
}
