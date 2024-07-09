package org.zerock.ziczone.dto.join;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.ziczone.domain.member.UserType;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyUserDTO {

    private String userName;

    private String email;

    private String password;

    private UserType userType;

    private String userIntro;

    private String companyNum;

    private String companyAddr;

    private String companyYear;

    private String companyLogo;

    private String companyCeo;

    //생성시간
    private String userCreate;
}
