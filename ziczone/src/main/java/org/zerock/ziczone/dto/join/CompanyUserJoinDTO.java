package org.zerock.ziczone.dto.join;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.UserType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyUserJoinDTO {

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
