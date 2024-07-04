package org.zerock.ziczone.domain.member;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.zerock.ziczone.domain.job.JobPosition;
import org.zerock.ziczone.domain.tech.TechStack;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "personalUser", fetch = FetchType.LAZY)
    private List<JobPosition> jobPositions;

    @OneToMany(mappedBy = "personalUser", fetch = FetchType.LAZY)
    private List<TechStack> techStacks;

}
