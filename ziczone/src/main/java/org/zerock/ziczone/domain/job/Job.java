package org.zerock.ziczone.domain.job;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;         // id

    @Column(length = 100)
    private String jobName;     // 직무 이름
}
