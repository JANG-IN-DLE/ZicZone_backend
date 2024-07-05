package org.zerock.ziczone.dto.join;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.UserType;

import java.time.LocalDate;
import java.util.List;

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

    private LocalDate companyYear;

    private String companyLogo;

    private String companyCeo;

    //생성시간
    private String userCreate;
}
