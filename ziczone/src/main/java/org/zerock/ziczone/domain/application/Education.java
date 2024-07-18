package org.zerock.ziczone.domain.application;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eduId;     // id

    @Column(length = 100, nullable = false)
    private String edu;     // 학력

    @Column(length = 100)
    private String credit;  // 학점

    @Column(length = 100)
    private String eduDate; // 졸업 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;  // 지원서 테이블

}
