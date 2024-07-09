package org.zerock.ziczone.domain.member;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.zerock.ziczone.domain.job.JobPosition;
import org.zerock.ziczone.domain.tech.TechStack;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personalId; // id

    @Column(length = 100, nullable = false)
    private String personalCareer; // [신입/경력]

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean isPersonalVisible; // 지원서 공개 여부 설정 (개인)

    @ColumnDefault("false")
    @Column(columnDefinition = "TINYINT(1)")
    private boolean isCompanyVisible; // 지원서 공개 여부 설정 (기업)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender; // 성별

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 유저 테이블

    @ToString.Exclude
    @OneToMany(mappedBy = "personalUser", fetch = FetchType.LAZY)
    private List<JobPosition> jobPositions; // 희망 직무 테이블

    @ToString.Exclude
    @OneToMany(mappedBy = "personalUser", fetch = FetchType.LAZY)
    private List<TechStack> techStacks; // 기술 스택 테이블

}
