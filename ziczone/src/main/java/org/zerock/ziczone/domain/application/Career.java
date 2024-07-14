package org.zerock.ziczone.domain.application;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careerId;          // id

    @Column(length = 100)
    private String careerName;      // 경력 회사 이름

    @Column(length = 100)
    private String careerJob;       // 경력 회사 직무

    @Column(length = 100)
    private String careerPosition;  // 경력 회사 직책

    @Column(length = 100)
    private String careerDate;      // 경력 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;          // 지원서 테이블

}
