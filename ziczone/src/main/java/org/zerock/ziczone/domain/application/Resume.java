package org.zerock.ziczone.domain.application;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.zerock.ziczone.domain.member.PersonalUser;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(length = 100, nullable = false)
    private String resumeName; // 지원서에 들어갈 이름

    @Column(length = 100, nullable = false)
    private String resumeDate; // 생년월일

    @Column(length = 100, nullable = false)
    private String resumeEmail; //이메일

    @Column(length = 100, nullable = false)
    private String phoneNum; // 전화번호

    @Column(length = 2048, nullable = false)
    private String resumePhotoUrl; // 지원서 증명사진 URL
    @Column(length = 2048, nullable = false)
    private String resumePhotoUUID; // 지원서 증명사진 UUID
    @Column(length = 2048, nullable = false)
    private String resumePhotoFileName; // 지원서 증명사진 FileName


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime resumeCreate; // 지원서 생성 날짜

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime resumeUpdate; // 지원서 업데이트 날짜

    @Column(length = 2048, nullable = false)
    private String personalStateUrl; // 자소서 PDF Url

    @Column(length = 2048, nullable = false)
    private String personalStateUUID; // 자소서 PDF UUID

    @Column(length = 2048, nullable = false)
    private String personalStateFileName; // 자소서 PDF FileName



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id")
    private PersonalUser personalUser; // 개인회원 테이블
}