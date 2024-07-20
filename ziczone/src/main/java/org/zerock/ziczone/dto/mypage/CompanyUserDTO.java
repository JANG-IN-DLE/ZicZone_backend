package org.zerock.ziczone.dto.mypage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.CompanyUser;
import org.zerock.ziczone.domain.member.User;

import javax.persistence.Column;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyUserDTO {

    private Long userId;

    private String companyLogoUrl;     // 기업 로고 이미지 경로 URL (스토리지)
    private String companyLogoUuid;     // 기업 로고 이미지 경로 UUID (스토리지)
    private String companyLogoFileName;     // 기업 로고 이미지 경로 FileName (스토리지)

    private UserDTO user;

    private Long companyId;

    private String companyNum;

    private String companyAddr;

    private String companyLogo;

    private String companyCeo;

    private LocalDate companyYear;
}
