package org.zerock.ziczone.dto.join;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.ziczone.domain.member.Gender;
import org.zerock.ziczone.domain.member.UserType;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalUserDTO {

    private String userName;
    private String email;
    private String password;
    private UserType userType;
    private String userIntro;
    private String personalCareer;
    private Gender gender;
    private List<Long> jobIds;
    private List<Long> techIds;

    //생성시간
    private String userCreate;
}
