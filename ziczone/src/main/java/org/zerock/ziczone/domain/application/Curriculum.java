package org.zerock.ziczone.domain.application;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curriId;           // id

    @Column(length = 100)
    private String curriContent;    // 교육 과정명

    @Column(length = 100)
    private String curriCompany;    // 교육 기관

    @Column(length = 100)
    private String curriDate;       // 교육 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;          // 지원서 테이블

}
