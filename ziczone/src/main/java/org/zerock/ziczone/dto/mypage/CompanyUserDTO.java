package org.zerock.ziczone.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.User;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyUserDTO {

    private Long userId;

//    private String email;

//    private String userName;

//    private String userIntro;

    private UserDTO user;

    private Long companyId;

    private String companyNum;

    private String companyAddr;

    private String companyLogo;

    private String companyCeo;

    private LocalDate companyYear;
}
