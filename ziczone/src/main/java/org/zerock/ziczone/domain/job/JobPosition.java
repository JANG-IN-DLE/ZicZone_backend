package org.zerock.ziczone.domain.job;

import lombok.*;
import org.zerock.ziczone.domain.member.PersonalUser;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JobPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userJobId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    private Job job;

}
