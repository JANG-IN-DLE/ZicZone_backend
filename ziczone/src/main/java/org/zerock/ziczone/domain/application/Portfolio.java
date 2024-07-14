package org.zerock.ziczone.domain.application;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portId;        // id

    @Column(length = 2048)
    private String portFile;    // PDF 파일명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;      // 지원서 테이블
}
