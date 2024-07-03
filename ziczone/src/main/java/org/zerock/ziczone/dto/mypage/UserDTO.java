package org.zerock.ziczone.dto.mypage;

import lombok.*;
import org.zerock.ziczone.domain.member.PersonalUser;
import org.zerock.ziczone.domain.member.UserType;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDTO {

    private Long userId;
    private String email;
    private String userName;
    private String userType;
    private String userIntro;

//    private LocalDateTime userCreate;
//    private PersonalUser personalUser;
//    private String password;
}
