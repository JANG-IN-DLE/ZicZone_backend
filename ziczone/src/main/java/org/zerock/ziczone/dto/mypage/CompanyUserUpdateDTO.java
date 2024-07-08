package org.zerock.ziczone.dto.mypage;

import lombok.*;

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

    private String companyLogo;

    private String companyUserPassword;

//    private String companyCeo; // 추후 변경할 수 있어 주석 처리

//    private LocalDate companyYear; // 추후 변경할 수 있어 주석 처리
}
