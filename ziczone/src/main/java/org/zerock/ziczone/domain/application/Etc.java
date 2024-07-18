package org.zerock.ziczone.domain.application;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Etc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long etcId;         // id

    @Column(length = 100)
    private String etcContent;  // 기타 내용

    @Column(length = 100)
    private String etcDate;     // 기타 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;      // 지원서 테이블
}
