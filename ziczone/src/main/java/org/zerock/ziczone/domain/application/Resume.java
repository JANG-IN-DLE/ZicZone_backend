package org.zerock.ziczone.domain.application;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.zerock.ziczone.domain.member.PersonalUser;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resumeId; // id

    @Column(length = 100, nullable = true)
    private String resumeName; // 지원서에 들어갈 이름

    @Column(length = 100, nullable = true)
    private String resumeDate; // 생년월일

    @Column(length = 100, nullable = true)
    private String resumeEmail; //이메일

    @Column(length = 100, nullable = true)
    private String phoneNum; // 전화번호

    @Column(columnDefinition = "TEXT", nullable = true)
    private String resumePhotoUrl; // 지원서 증명사진 URL
    @Column(columnDefinition = "TEXT", nullable = true)
    private String resumePhotoUuid; // 지원서 증명사진 UUID
    @Column(columnDefinition = "TEXT", nullable = true)
    private String resumePhotoFileName; // 지원서 증명사진 FileName


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime resumeCreate; // 지원서 생성 날짜

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime resumeUpdate; // 지원서 업데이트 날짜

    @Column(columnDefinition = "TEXT", nullable = true)
    private String personalStateUrl; // 자소서 PDF Url

    @Column(columnDefinition = "TEXT", nullable = true)
    private String personalStateUuid; // 자소서 PDF UUID

    @Column(columnDefinition = "TEXT", nullable = true)
    private String personalStateFileName; // 자소서 PDF FileName



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser; // 개인회원 테이블
}