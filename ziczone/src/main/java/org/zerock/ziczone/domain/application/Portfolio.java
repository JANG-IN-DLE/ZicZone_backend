package org.zerock.ziczone.domain.application;

import lombok.*;
import org.w3c.dom.Text;

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

    @Column(columnDefinition = "TEXT", nullable = true)
    private String portFileUrl;    // PDF 파일명 Url

    @Column(columnDefinition = "TEXT", nullable = true)
    private String portFileUuid;    // PDF 파일명 UUID

    @Column(columnDefinition = "TEXT", nullable = true)
    private String portFileName;    // PDF 파일명 FileName

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;      // 지원서 테이블
}