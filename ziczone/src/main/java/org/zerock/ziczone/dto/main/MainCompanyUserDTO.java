package org.zerock.ziczone.dto.main;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.Gender;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainCompanyUserDTO {

    private String userName;

    private String email;

    private String companyLogo;
}
