package org.zerock.ziczone.domain.application;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Archive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long archId;        // id

    @Column(length = 100)
    private String archGit;     // Git 주소

    @Column(length = 100)
    private String archNotion;  // 노션 주소

    @Column(length = 100)
    private String archBlog;    // 블로그 주소

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;      // 지원서 테이블

}
