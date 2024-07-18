package org.zerock.ziczone.dto.mypage;

import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;

@Builder
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyUserUpdateDTO {


//    private String email;  // 추후 변경할 수 있어 주석 처리

    private String userName;

    private String userIntro;

//    private String companyNum; // 추후 변경할 수 있어 주석 처리

    private String companyAddr;

    private String companyLogoUrl;     // 기업 로고 이미지 경로 URL (스토리지)
    private String companyLogoUUID;     // 기업 로고 이미지 경로 UUID (스토리지)
    private String companyLogoFileName;     // 기업 로고 이미지 경로 FileName (스토리지)

    private String changePassword;

    private String currentPassword;

//    private String companyCeo; // 추후 변경할 수 있어 주석 처리

//    private LocalDate companyYear; // 추후 변경할 수 있어 주석 처리
}
