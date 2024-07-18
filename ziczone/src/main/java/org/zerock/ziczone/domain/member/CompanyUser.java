package org.zerock.ziczone.domain.member;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder(toBuilder = true) // toBuilder 속성 추가
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompanyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;         // id

    @Column(length = 100, nullable = false)
    private String companyNum;      // 기업 사업자 등록번호

    @Column(length = 100, nullable = false)
    private String companyAddr;     // 기업 주소

    @Column(nullable = false)
    private LocalDate companyYear;  // 기업 설립연도

    @Column(nullable = false,length = 2048)
    private String companyLogoUrl;     // 기업 로고 이미지 경로 URL (스토리지)
    @Column(nullable = false,length = 2048)
    private String companyLogoUUID;     // 기업 로고 이미지 경로 UUID (스토리지)
    @Column(nullable = false,length = 2048)
    private String companyLogoFileName;     // 기업 로고 이미지 경로 FileName (스토리지)



//    @Column(nullable = false,length = 500)
//    private String companyLogoUrl;     // 기업 로고 이미지 경로 URL

    @Column(nullable = false)
    private String companyCeo;      // 대표 이름

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;              // 유저 테이블

}
