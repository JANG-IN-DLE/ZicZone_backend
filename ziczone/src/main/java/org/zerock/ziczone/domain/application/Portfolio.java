package org.zerock.ziczone.domain.application;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long portId;        // id

    @Column(length = 2048, nullable = false)
    private String portFileUrl;    // PDF 파일명 Url

    @Column(length = 2048, nullable = false)
    private String portFileUuid;    // PDF 파일명 UUID

    @Column(length = 2048, nullable = false)
    private String portFileFileName;    // PDF 파일명 FileName

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;      // 지원서 테이블
}